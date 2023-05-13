package it.polito.wa2.g29.server.integration.controller

import it.polito.wa2.g29.server.enums.AttachmentType
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.*
import it.polito.wa2.g29.server.repository.*
import it.polito.wa2.g29.server.service.TicketStatusChangeService
import it.polito.wa2.g29.server.utils.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatControllerIT : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var ticketStatusChangeService: TicketStatusChangeService

    @Autowired
    private lateinit var messageRepository: MessageRepository

    @Autowired
    private lateinit var expertRepository: ExpertRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var profileRepository: ProfileRepository

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    lateinit var testProducts: List<Product>
    lateinit var testProfiles: List<Profile>
    lateinit var testExperts: List<Expert>
    lateinit var testTickets: List<Ticket>
    lateinit var ticketWithMessages: Ticket
    lateinit var ticketWithoutMessages: Ticket
    lateinit var ticketExpert: Expert
    val messageWithAttachmentIndex = 0

    @BeforeAll
    fun prepare() {
        testProducts = ProductTestUtils.insertProducts(productRepository)
        testProfiles = ProfileTestUtils.insertProfiles(profileRepository)
        testExperts = ExpertTestUtils.insertExperts(expertRepository)
        TicketTestUtils.products = testProducts
        TicketTestUtils.profiles = testProfiles
        testTickets = TicketTestUtils.insertTickets(ticketRepository)
        ticketExpert = testExperts[0]
        ticketWithMessages = testTickets[0]
        ticketWithoutMessages = testTickets[1]
        TicketTestUtils.startTicket(ticketRepository, ticketWithMessages, ticketExpert, TicketPriority.LOW)
        val messages = TestChatUtils.getMessages(ticketWithMessages, ticketExpert)
        messages[messageWithAttachmentIndex].apply {
            attachments = setOf(
                Attachment(
                    "AttachmentName",
                    byteArrayOf(0x00, 0x01, 0x02),
                    AttachmentType.OTHER,
                    this
                )
            )
        }
        TestChatUtils.addMessages(messageRepository, messages, ticketWithMessages, ticketExpert)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/chats/{ticketId}/messages
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getAllChatMessagesWithChatWithoutMessages() {
        mockMvc
            .perform(get("/API/chats/${ticketWithoutMessages.id!!}/messages").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isEmpty
            )
    }

    @Test
    @Transactional
    fun getAllChatMessagesWithChatWithManyMessages() {
        mockMvc
            .perform(get("/API/chats/${ticketWithMessages.id!!}/messages").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isNotEmpty,
                jsonPath("$[*].messageId").exists(),
                jsonPath("$[*].sender").exists(),
                jsonPath("$[*].content").exists(),
                jsonPath("$[*].attachments").isArray,
                jsonPath("$[*].attachments[0]").isNotEmpty,
                jsonPath("$[*].time").exists()
            )
    }

    @Test
    @Transactional
    @Rollback
    fun getAllChatMessagesInAnyTicketStatus() {
        val chatStatusTransactions =
            listOf(
                TicketStatus.OPEN,
                TicketStatus.IN_PROGRESS,
                TicketStatus.RESOLVED,
                TicketStatus.CLOSED,
                TicketStatus.REOPENED
            )

        chatStatusTransactions.forEach {
            ExpertTestUtils.addTicketStatusChange(
                ticketStatusChangeService,
                ticketExpert,
                ticketWithMessages,
                it,
                UserType.MANAGER,
                null
            )
            mockMvc
                .perform(get("/API/chats/${ticketWithMessages.id!!}/messages").contentType("application/json"))
                .andExpectAll(
                    status().isOk,
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$").isArray,
                    jsonPath("$").isNotEmpty
                )
        }
    }

    @Test
    fun getAllChatMessagesWithNonExistingTicketId() {
        val ticketId = Int.MAX_VALUE

        mockMvc
            .perform(get("/API/chats/$ticketId/messages").contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, -2, Int.MIN_VALUE]
    )
    fun getAllChatMessagesWithInvalidTicketIdValue(id: Int) {
        mockMvc
            .perform(get("/API/chats/${id}/messages").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun getAllChatMessagesWithInvalidTicketIdType() {
        mockMvc
            .perform(get("/API/chats/wrongTypeId/messages").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    /////////////////////////////////////////////////////////////////////
    ////// POST `/API/chats/{ticketId}/messages`
    /////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @EnumSource(UserType::class, names = ["CUSTOMER", "EXPERT"])
    @Transactional
    @Rollback
    fun sendMessage(userType: UserType) {
        val contentBytes = "file content".toByteArray()
        val file1 = MockMultipartFile("attachments", "file1.pdf", "application/pdf", contentBytes)
        val file2 = MockMultipartFile("attachments", "file2.txt", "text/plain", contentBytes)

        mockMvc
            .perform(
                multipart("/API/chats/${ticketWithMessages.id!!}/messages")
                    .file(file1)
                    .file(file2)
                    .param("sender", userType.toString())
                    .param("content", "Message")
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            .andExpectAll(
                status().isCreated,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.messageId").isNumber
            )
    }

    @Test
    fun sendMessageNonExistingTicket() {
        mockMvc
            .perform(
                multipart("/API/chats/${Int.MAX_VALUE}/messages")
                    .param("sender", UserType.EXPERT.toString())
                    .param("content", "Message")
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            .andExpectAll(
                status().isNotFound,
                content().string("")
            )
    }


    @Test
    fun sendMessageWithManagerShouldBeForbidden() {
        mockMvc
            .perform(
                multipart("/API/chats/${ticketWithMessages.id!!}/messages")
                    .param("sender", UserType.MANAGER.toString())
                    .param("content", "Message")
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            .andExpectAll(
                status().isUnprocessableEntity,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.error").isString
            )
    }

    @Test
    @Transactional
    @Rollback
    fun sendMessageWhenTicketIsInactive() {
        val contentBytes = "file content".toByteArray()
        val file1 = MockMultipartFile("attachments", "file1.pdf", "application/pdf", contentBytes)
        val file2 = MockMultipartFile("attachments", "file2.txt", "text/plain", contentBytes)
        val chatStatusTransactions =
            listOf(
                TicketStatus.OPEN,
                TicketStatus.IN_PROGRESS,
                TicketStatus.RESOLVED,
                TicketStatus.CLOSED,
                TicketStatus.REOPENED
            )

        chatStatusTransactions.forEach {
            ExpertTestUtils.addTicketStatusChange(
                ticketStatusChangeService,
                ticketExpert,
                ticketWithMessages,
                it,
                UserType.MANAGER,
                null
            )
            if (it != TicketStatus.IN_PROGRESS && it != TicketStatus.RESOLVED)
                mockMvc
                    .perform(
                        multipart("/API/chats/${ticketWithMessages.id!!}/messages")
                            .file(file1)
                            .file(file2)
                            .param("sender", UserType.CUSTOMER.toString())
                            .param("content", "Message")
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    )
                    .andExpectAll(
                        status().isUnprocessableEntity,
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.error").isString
                    )
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["FakeSender", "ADMIN"]
    )
    fun sendMessageWithInvalidSender(sender: String) {
        mockMvc
            .perform(
                multipart("/API/chats/${ticketWithMessages.id!!}/messages")
                    .param("sender", sender)
                    .param("content", "Message")
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            .andExpectAll(
                status().isUnprocessableEntity,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.error").isString
            )
    }

    @ParameterizedTest
    @NullAndEmptySource
    fun sendMessageWithEmptySender(sender: String?) {
        mockMvc
            .perform(
                multipart("/API/chats/${ticketWithMessages.id!!}/messages")
                    .param("sender", sender)
                    .param("content", "Message")
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            .andExpectAll(
                status().isUnprocessableEntity,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.error").isString
            )
    }


    @ParameterizedTest
    @NullAndEmptySource
    fun sendMessageWithInvalidContent(content: String?) {
        mockMvc
            .perform(
                multipart("/API/chats/${ticketWithMessages.id!!}/messages")
                    .param("sender", UserType.EXPERT.toString())
                    .param("content", content)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            .andExpectAll(
                status().isUnprocessableEntity,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.error").isString
            )
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET `/API/chats/{ticketId}/messages/{messageId}/attachments/{attachmentId}`
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun getAttachment() {
        val messages = listOf(Message(UserType.CUSTOMER, "Message2", ticketWithMessages, null))
        messages[0].apply {
            attachments = setOf(
                Attachment(
                    "Filename1.png",
                    byteArrayOf(0x00, 0x01, 0x02),
                    AttachmentType.PNG,
                    this
                ),
                Attachment(
                    "Filename2.jpeg",
                    byteArrayOf(0x03, 0x04, 0x06),
                    AttachmentType.JPEG,
                    this
                ),
                Attachment(
                    "Filename3.pdf",
                    byteArrayOf(0x07, 0x08, 0x09),
                    AttachmentType.PDF,
                    this
                ),
                Attachment(
                    "Filename4.doc",
                    byteArrayOf(0x00, 0x01, 0x02, 0x00, 0x01, 0x02, 0x00, 0x01, 0x02, 0x00, 0x01, 0x02),
                    AttachmentType.DOC,
                    this
                ),
                Attachment(
                    "Filename5.bin",
                    byteArrayOf(0x00, 0x01, 0x02, 0x02, 0x02, 0x02),
                    AttachmentType.OTHER,
                    this
                )
            )
        }
        TestChatUtils.addMessages(messageRepository, messages, ticketWithMessages, ticketExpert)
        val expectedMessage = messages[0]

        expectedMessage.attachments.forEach {
            mockMvc
                .perform(
                    get("/API/chats/${ticketWithMessages.id!!}/messages/${expectedMessage.id}/attachments/${it.id}")
                        .contentType("application/json")
                )
                .andExpectAll(
                    status().isOk,
                    content().contentType(MediaTypeUtil.attachmentTypeToMediaType(it.type)),
                    content().bytes(it.file),
                    header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"${it.name}\""
                    )
                )
        }
    }

    @Test
    @Transactional
    @Rollback
    fun getAttachmentTicketIdNotFound() {
        val messages = listOf(Message(UserType.CUSTOMER, "Message2", ticketWithMessages, null))
        messages[0].apply {
            attachments = setOf(
                Attachment(
                    "Filename.png",
                    byteArrayOf(0x00, 0x01, 0x02),
                    AttachmentType.PNG,
                    this
                )
            )
        }
        TestChatUtils.addMessages(messageRepository, messages, ticketWithMessages, ticketExpert)
        val expectedMessage = messages[0]
        val expectedAttachment = expectedMessage.attachments.toList()[0]

        mockMvc
            .perform(
                get("/API/chats/${Int.MAX_VALUE}/messages/${expectedMessage.id}/attachments/${expectedAttachment.id}").contentType(
                    "application/json"
                )
            )
            .andExpectAll(
                status().isNotFound,
                content().string("")
            )
    }

    @Test
    @Transactional
    @Rollback
    fun getAttachmentMessageIdNotFound() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TicketTestUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
        val messages = listOf(Message(UserType.CUSTOMER, "Message2", ticket, null))
        messages[0].apply {
            attachments = setOf(
                Attachment(
                    "Filename.png",
                    byteArrayOf(0x00, 0x01, 0x02),
                    AttachmentType.PNG,
                    this
                )
            )
        }
        TestChatUtils.addMessages(messageRepository, messages, ticket, ticketExpert)
        val expectedMessage = messages[0]
        val expectedAttachment = expectedMessage.attachments.toList()[0]

        mockMvc
            .perform(
                get("/API/chats/${ticket.id!!}/messages/${Int.MAX_VALUE}/attachments/${expectedAttachment.id}").contentType(
                    "application/json"
                )
            )
            .andExpectAll(
                status().isNotFound,
                content().string("")
            )
    }

    @Test
    @Transactional
    @Rollback
    fun getAttachmentAttachmentIdNotFound() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TicketTestUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
        val messages = listOf(Message(UserType.CUSTOMER, "Message2", ticket, null))
        messages[0].apply {
            attachments = setOf(
                Attachment(
                    "Filename.png",
                    byteArrayOf(0x00, 0x01, 0x02),
                    AttachmentType.PNG,
                    this
                )
            )
        }
        TestChatUtils.addMessages(messageRepository, messages, ticket, ticketExpert)
        val expectedMessage = messages[0]

        mockMvc
            .perform(
                get("/API/chats/${ticket.id!!}/messages/${expectedMessage.id}/attachments/${Int.MAX_VALUE}").contentType(
                    "application/json"
                )
            )
            .andExpectAll(
                status().isNotFound,
                content().string("")
            )
    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, Int.MIN_VALUE]
    )
    @Transactional
    @Rollback
    fun getAttachmentTicketIdInvalid(ticketId: Int) {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TicketTestUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
        val messages = listOf(Message(UserType.CUSTOMER, "Message2", ticket, null))
        messages[0].apply {
            attachments = setOf(
                Attachment(
                    "Filename.png",
                    byteArrayOf(0x00, 0x01, 0x02),
                    AttachmentType.PNG,
                    this
                )
            )
        }
        TestChatUtils.addMessages(messageRepository, messages, ticket, ticketExpert)
        val expectedMessage = messages[0]
        val expectedAttachment = expectedMessage.attachments.toList()[0]
        mockMvc
            .perform(
                get("/API/chats/${ticketId}/messages/${expectedMessage.id}/attachments/${expectedAttachment.id}").contentType(
                    "application/json"
                )
            )
            .andExpectAll(
                status().isUnprocessableEntity,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.error").isString
            )
    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, Int.MIN_VALUE]
    )
    @Transactional
    @Rollback
    fun getAttachmentMessageIdInvalid(messageId: Int) {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TicketTestUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
        val messages = listOf(Message(UserType.CUSTOMER, "Message2", ticket, null))
        messages[0].apply {
            attachments = setOf(
                Attachment(
                    "Filename.png",
                    byteArrayOf(0x00, 0x01, 0x02),
                    AttachmentType.PNG,
                    this
                )
            )
        }
        TestChatUtils.addMessages(messageRepository, messages, ticket, ticketExpert)
        val expectedMessage = messages[0]
        val expectedAttachment = expectedMessage.attachments.toList()[0]

        mockMvc
            .perform(
                get("/API/chats/${ticket.id!!}/messages/${messageId}/attachments/${expectedAttachment.id}").contentType(
                    "application/json"
                )
            )
            .andExpectAll(
                status().isUnprocessableEntity,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.error").isString
            )

    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, Int.MIN_VALUE]
    )
    @Transactional
    fun getAttachmentAttachmentIdInvalid(attachmentId: Int) {
        val messageWithAttachment = ticketWithMessages.messages.toList()[messageWithAttachmentIndex]
        mockMvc
            .perform(
                get("/API/chats/${ticketWithMessages.id!!}/messages/${messageWithAttachment.id}/attachments/${attachmentId}").contentType(
                    "application/json"
                )
            )
            .andExpectAll(
                status().isUnprocessableEntity,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.error").isString
            )
    }
}