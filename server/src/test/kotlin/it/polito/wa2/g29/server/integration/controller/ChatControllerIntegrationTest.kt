package it.polito.wa2.g29.server.integration.controller

import it.polito.wa2.g29.server.dto.MessageDTO
import it.polito.wa2.g29.server.enums.AttachmentType
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.*
import it.polito.wa2.g29.server.repository.*
import it.polito.wa2.g29.server.service.TicketStatusChangeService
import it.polito.wa2.g29.server.utils.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockMultipartFile
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatControllerIntegrationTest : AbstractTestcontainersTest() {
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

    @BeforeAll
    fun prepare() {
        productRepository.deleteAllInBatch()
        profileRepository.deleteAllInBatch()
        ticketRepository.deleteAllInBatch()
        expertRepository.deleteAllInBatch()
        testProducts = TestProductUtils.insertProducts(productRepository)
        testProfiles = TestProfileUtils.insertProfiles(profileRepository)
        testExperts = TestExpertUtils.insertExperts(expertRepository)
        TestTicketUtils.products = testProducts
        TestTicketUtils.profiles = testProfiles
        testTickets = TestTicketUtils.insertTickets(ticketRepository)
    }

    @AfterAll
    fun prune() {
        TestChatUtils.deleteAllMessages(messageRepository, testTickets, testExperts)
        ticketRepository.deleteAll()
        productRepository.deleteAllInBatch()
        profileRepository.deleteAllInBatch()
        expertRepository.deleteAll()
    }

    @BeforeEach
    fun setup() {
        TestChatUtils.deleteAllMessages(messageRepository, testTickets, testExperts)
        ticketRepository.deleteAll()
        testTickets = TestTicketUtils.insertTickets(ticketRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/chats/{ticketId}/messages
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getAllChatMessagesWithChatWithoutMessages() {
        val ticket = testTickets[0]

        mockMvc
            .perform(get("/API/chats/${ticket.id!!}/messages").contentType("application/json"))
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
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
        val messages = TestChatUtils.getMessages(ticket, ticketExpert)
        messages[0].apply {
            attachments = setOf(
                Attachment(
                    "AttachmentName.bin",
                    byteArrayOf(0x00, 0x01, 0x02),
                    AttachmentType.OTHER,
                    this
                ),
                Attachment(
                    "AttachmentName.pdf",
                    byteArrayOf(0x00, 0x01, 0x02),
                    AttachmentType.PDF,
                    this
                )
            )
        }
        TestChatUtils.addMessages(messageRepository, messages, ticket, ticketExpert)

        mockMvc
            .perform(get("/API/chats/${ticket.id!!}/messages").contentType("application/json"))
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
    fun getAllChatMessagesWithOneMessageByCustomer() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
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
            .perform(get("/API/chats/${ticket.id!!}/messages").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath<List<MessageDTO>>("$", hasSize(messages.size)),
                jsonPath("$[0].messageId").value(expectedMessage.id),
                jsonPath("$[0].sender").value(expectedMessage.sender.toString()),
                jsonPath("$[0].expertId").value(ticketExpert.id),
                jsonPath("$[0].content").value(expectedMessage.content),
                jsonPath("$[0].attachments").isArray,
                jsonPath("$[0].attachments[0].attachmentId").value(expectedAttachment.id),
                jsonPath("$[0].attachments[0].name").value(expectedAttachment.name),
                jsonPath("$[0].attachments[0].type").value(expectedAttachment.type.toString()),
                jsonPath("$[0].time").value(expectedMessage.time),
            )
    }

    @Test
    @Transactional
    fun getAllChatMessagesWithOneMessageByExpert() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
        val messages = listOf(Message(UserType.EXPERT, "Message1", ticket, ticketExpert))
        messages[0].apply {
            attachments = setOf(
                Attachment(
                    "Filename.doc",
                    byteArrayOf(0x00, 0x01, 0x02),
                    AttachmentType.DOC,
                    this
                )
            )
        }
        TestChatUtils.addMessages(messageRepository, messages, ticket, ticketExpert)
        val expectedMessage = messages[0]
        val expectedAttachment = expectedMessage.attachments.toList()[0]

        mockMvc
            .perform(get("/API/chats/${ticket.id!!}/messages").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath<List<MessageDTO>>("$", hasSize(messages.size)),
                jsonPath("$[0].messageId").value(expectedMessage.id),
                jsonPath("$[0].sender").value(expectedMessage.sender.toString()),
                jsonPath("$[0].expertId").value(expectedMessage.expert!!.id),
                jsonPath("$[0].content").value(expectedMessage.content),
                jsonPath("$[0].attachments").isArray,
                jsonPath("$[0].attachments[0].attachmentId").value(expectedAttachment.id),
                jsonPath("$[0].attachments[0].name").value(expectedAttachment.name),
                jsonPath("$[0].attachments[0].type").value(expectedAttachment.type.toString()),
                jsonPath("$[0].time").value(expectedMessage.time),
            )
    }

    @Test
    @Transactional
    fun getAllChatMessagesInAnyTicketStatus() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
        val messages = listOf(Message(UserType.EXPERT, "Message1", ticket, ticketExpert))
        TestChatUtils.addMessages(messageRepository, messages, ticket, ticketExpert)
        val chatStatusTransactions =
            listOf(
                TicketStatus.OPEN,
                TicketStatus.IN_PROGRESS,
                TicketStatus.RESOLVED,
                TicketStatus.CLOSED,
                TicketStatus.REOPENED
            )

        chatStatusTransactions.forEach {
            TestExpertUtils.addTicketStatusChange(
                ticketStatusChangeService,
                ticketExpert,
                ticket,
                it,
                UserType.MANAGER,
                null
            )
            mockMvc
                .perform(get("/API/chats/${ticket.id!!}/messages").contentType("application/json"))
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

    @Test
    fun getAllChatMessagesWithInvalidTicketIdValue() {
        val ticketIdWrongValues = listOf(0, -1, -2, Int.MIN_VALUE)

        ticketIdWrongValues.forEach {
            mockMvc
                .perform(get("/API/chats/${it}/messages").contentType("application/json"))
                .andExpect(status().isUnprocessableEntity)
        }
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

    @Test
    @Transactional
    fun sendMessageWithCustomer() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.CRITICAL)
        val contentBytes = "file content".toByteArray()
        val file1 = MockMultipartFile("attachments", "file1.pdf", "application/pdf", contentBytes)
        val file2 = MockMultipartFile("attachments", "file2.txt", "text/plain", contentBytes)

        mockMvc
            .perform(
                multipart("/API/chats/${ticket.id!!}/messages")
                    .file(file1)
                    .file(file2)
                    .param("sender", UserType.CUSTOMER.toString())
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
    @Transactional
    fun sendMessageWithExpert() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.HIGH)
        val contentBytes = "file content".toByteArray()
        val file1 = MockMultipartFile("attachments", "file1.jpg", "image/jpeg", contentBytes)
        val file2 = MockMultipartFile("attachments", "file2.png", "image/png", contentBytes)

        mockMvc
            .perform(
                multipart("/API/chats/${ticket.id!!}/messages")
                    .file(file1)
                    .file(file2)
                    .param("sender", UserType.EXPERT.toString())
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
                multipart("/API/chats/1/messages")
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
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)

        mockMvc
            .perform(
                multipart("/API/chats/${ticket.id!!}/messages")
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
    fun sendMessageWhenTicketIsInactive() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
        val messages = listOf(Message(UserType.EXPERT, "Message1", ticket, ticketExpert))
        TestChatUtils.addMessages(messageRepository, messages, ticket, ticketExpert)
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
            TestExpertUtils.addTicketStatusChange(
                ticketStatusChangeService,
                ticketExpert,
                ticket,
                it,
                UserType.MANAGER,
                null
            )
            if (it != TicketStatus.IN_PROGRESS && it != TicketStatus.RESOLVED)
                mockMvc
                    .perform(
                        multipart("/API/chats/${ticket.id!!}/messages")
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

    @Test
    fun sendMessageWithInvalidSender() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.HIGH)
        val invalidSender = listOf("", null, "FakeSender", "ADMIN")

        invalidSender.forEach {
            mockMvc
                .perform(
                    multipart("/API/chats/${ticket.id!!}/messages")
                        .param("sender", it)
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

    @Test
    fun sendMessageWithInvalidContent() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.HIGH)
        val invalidContent = listOf("", null, " ", "  ")

        invalidContent.forEach {
            mockMvc
                .perform(
                    multipart("/API/chats/${ticket.id!!}/messages")
                        .param("sender", UserType.EXPERT.toString())
                        .param("content", it)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                )
                .andExpectAll(
                    status().isUnprocessableEntity,
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.error").isString
                )
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET `/API/chats/{ticketId}/messages/{messageId}/attachments/{attachmentId}`
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    fun getAttachment() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
        val messages = listOf(Message(UserType.CUSTOMER, "Message2", ticket, null))
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
        TestChatUtils.addMessages(messageRepository, messages, ticket, ticketExpert)
        val expectedMessage = messages[0]

        expectedMessage.attachments.forEach {
            mockMvc
                .perform(
                    get("/API/chats/${ticket.id!!}/messages/${expectedMessage.id}/attachments/${it.id}")
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
    fun getAttachmentTicketIdNotFound() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
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
    fun getAttachmentMessageIdNotFound() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
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
    fun getAttachmentAttachmentIdNotFound() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
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

    @Test
    @Transactional
    fun getAttachmentTicketIdInvalid() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
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
        val invalidIds = listOf("invalid", -1, 0)

        invalidIds.forEach {
            mockMvc
                .perform(
                    get("/API/chats/${it}/messages/${expectedMessage.id}/attachments/${expectedAttachment.id}").contentType(
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

    @Test
    @Transactional
    fun getAttachmentMessageIdInvalid() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
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
        val invalidIds = listOf("invalid", -1, 0)

        invalidIds.forEach {
            mockMvc
                .perform(
                    get("/API/chats/${ticket.id!!}/messages/${it}/attachments/${expectedAttachment.id}").contentType(
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

    @Test
    @Transactional
    fun getAttachmentAttachmentIdInvalid() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TestTicketUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
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
        val invalidIds = listOf("invalid", -1, 0)

        invalidIds.forEach {
            mockMvc
                .perform(
                    get("/API/chats/${ticket.id!!}/messages/${expectedMessage.id}/attachments/${it}").contentType(
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
}