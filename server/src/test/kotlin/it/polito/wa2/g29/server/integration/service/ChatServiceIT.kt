package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.*
import it.polito.wa2.g29.server.enums.AttachmentType
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.exception.*
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.*
import it.polito.wa2.g29.server.repository.*
import it.polito.wa2.g29.server.service.ChatService
import it.polito.wa2.g29.server.service.TicketStatusChangeService
import it.polito.wa2.g29.server.utils.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatServiceIT : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var chatService: ChatService

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
    lateinit var ticketExpert: Expert
    val messageWithAttachmentIndex = 0
    val messageWithoutAttachmentIndex = 1

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
    ////// getMessagesByTicketId
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    fun getMessagesByTicketIdWithNoMessages() {
        val ticket = testTickets[messageWithoutAttachmentIndex]

        SecurityTestUtils.setClient(ticket.customer.email)
        val actualMessageDTOs = chatService.getMessagesByTicketId(ticket.id!!)

        assert(actualMessageDTOs.isEmpty())
    }

    @Test
    @Transactional
    @Rollback
    fun getMessagesByTicketIdWithManyMessages() {
        val messages = ticketWithMessages.messages
        val expectedMessageDTOs = messages.map { it.toDTO() }

        SecurityTestUtils.setExpert(ticketExpert.email)
        val actualMessageDTOs = chatService.getMessagesByTicketId(ticketWithMessages.id!!)

        assert(actualMessageDTOs.isNotEmpty())
        assert(expectedMessageDTOs.size == actualMessageDTOs.size)
        assert(expectedMessageDTOs == actualMessageDTOs)
        for (i in 0 until actualMessageDTOs.size - 1) {
            assert(actualMessageDTOs[i].time <= actualMessageDTOs[i + 1].time)
        }
    }

    @Test
    fun getMessagesByTicketIdNotFound() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        assertThrows<TicketNotFoundException> {
            chatService.getMessagesByTicketId(Int.MAX_VALUE)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// addMessageWithAttachments
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun addMessageWithAttachmentsManagerCannotSendMsg() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TicketTestUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
        val newMessageDTO = TestChatUtils.getNewMessageDTO("Message", null)

        SecurityTestUtils.setManager()
        assertThrows<AccessDeniedException> {
            chatService.addMessageWithAttachments(ticket.id!!, newMessageDTO)
        }
    }

    @Test
    @Transactional
    @Rollback
    fun addMessageWithAttachmentsTicketIdNotFound() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TicketTestUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
        val newMessageDTO = TestChatUtils.getNewMessageDTO("Message", null)

        SecurityTestUtils.setClient(ticket.customer.email)
        assertThrows<TicketNotFoundException> {
            chatService.addMessageWithAttachments(Int.MAX_VALUE, newMessageDTO)
        }
    }

    @Test
    @Transactional
    @Rollback
    fun addMessageWithAttachmentsChatInactive() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TicketTestUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
        val newMessageDTO = TestChatUtils.getNewMessageDTO("Message", AttachmentType.OTHER)
        val chatStatusTransactions =
            listOf(TicketStatus.OPEN, TicketStatus.IN_PROGRESS, TicketStatus.CLOSED, TicketStatus.REOPENED)

        chatStatusTransactions.forEach {
            SecurityTestUtils.setManager()
            ExpertTestUtils.addTicketStatusChange(
                ticketStatusChangeService,
                ticketExpert,
                ticket,
                it,
                UserType.MANAGER,
                null
            )
            if (it != TicketStatus.IN_PROGRESS) {
                SecurityTestUtils.setClient(ticket.customer.email)
                assertThrows<ChatIsInactiveException> {
                    chatService.addMessageWithAttachments(ticket.id!!, newMessageDTO)
                }
            }
        }
    }

    @Test
    @Transactional
    @Rollback
    fun addMessageWithAttachmentsWithoutAttachment() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TicketTestUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)
        val newMessageDTO = TestChatUtils.getNewMessageDTO("Message", null)

        SecurityTestUtils.setExpert(ticketExpert.email)
        val messageId = chatService.addMessageWithAttachments(ticket.id!!, newMessageDTO).messageId

        val actualMessage = messageRepository.findByIdOrNull(messageId)
        assert(actualMessage != null)
        assert(newMessageDTO.content == actualMessage?.content)
        assert(ticketExpert == actualMessage?.expert)
        assert(ticket == actualMessage?.ticket)
        assert(actualMessage?.time != null)
        assert(actualMessage?.attachments?.isEmpty() ?: false)
    }

    @ParameterizedTest
    @EnumSource(AttachmentType::class)
    @Transactional
    @Rollback
    fun addMessageWithAttachmentsWithDifferentAttachmentType(attachmentType: AttachmentType) {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        TicketTestUtils.startTicket(ticketRepository, ticket, ticketExpert, TicketPriority.LOW)

        val newMessageDTO = TestChatUtils.getNewMessageDTO("Message", attachmentType)

        SecurityTestUtils.setClient(ticket.customer.email)
        val messageId = chatService.addMessageWithAttachments(ticket.id!!, newMessageDTO).messageId

        val actualMessage = messageRepository.findByIdOrNull(messageId)
        assert(actualMessage != null)
        assert(newMessageDTO.content == actualMessage?.content)
        assert(ticketExpert == actualMessage?.expert)
        assert(ticket == actualMessage?.ticket)
        assert(actualMessage?.time != null)
        assert(actualMessage?.attachments?.size == 1)
        val expectedAttachment = newMessageDTO.attachments?.get(0)
        val actualAttachment = actualMessage?.toDTO()?.attachments?.get(0)
        assert(expectedAttachment?.name == actualAttachment?.name)
        assert(attachmentType.toString() == actualAttachment?.type)

    }

    /////////////////////////////////////////////////////////////////////
    ////// getAttachments
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    fun getAttachmentsWithTicketIdNotFound() {
        val messageWithAttachment = ticketWithMessages.messages.toList()[messageWithAttachmentIndex]
        val expectedAttachment = messageWithAttachment.attachments.toList()[0]
        assertThrows<TicketNotFoundException> {
            chatService.getAttachment(Int.MAX_VALUE, messageWithAttachment.id!!, expectedAttachment.id!!)
        }
    }

    @Test
    @Transactional
    fun getAttachmentsWithMessageIdNotFound() {
        val messageWithAttachment = ticketWithMessages.messages.toList()[messageWithAttachmentIndex]
        val expectedAttachment = messageWithAttachment.attachments.toList()[0]
        SecurityTestUtils.setClient(ticketWithMessages.customer.email)
        assertThrows<MessageNotFoundException> {
            chatService.getAttachment(ticketWithMessages.id!!, Int.MAX_VALUE, expectedAttachment.id!!)
        }
    }

    @Test
    @Transactional
    fun getAttachmentsWithAttachmentIdNotFound() {
        SecurityTestUtils.setClient(ticketWithMessages.customer.email)
        val messageWithAttachment = ticketWithMessages.messages.toList()[messageWithAttachmentIndex]
        assertThrows<AttachmentNotFoundException> {
            chatService.getAttachment(ticketWithMessages.id!!, messageWithAttachment.id!!, Int.MAX_VALUE)
        }
    }

    @Test
    @Transactional
    fun getAttachmentsClient() {
        val messageWithAttachment = ticketWithMessages.messages.toList()[messageWithAttachmentIndex]
        val expectedAttachment = messageWithAttachment.attachments.toList()[0]
        SecurityTestUtils.setClient(ticketWithMessages.customer.email)
        val actualAttachment =
            chatService.getAttachment(ticketWithMessages.id!!, messageWithAttachment.id!!, expectedAttachment.id!!)
        assert(expectedAttachment.name == actualAttachment.name)
        assert(expectedAttachment.type == actualAttachment.type)
        assert(expectedAttachment.file.contentEquals(actualAttachment.file))
    }

    @Test
    @Transactional
    fun getAttachmentsExpert() {
        val messageWithAttachment = ticketWithMessages.messages.toList()[messageWithAttachmentIndex]
        val expectedAttachment = messageWithAttachment.attachments.toList()[0]
        SecurityTestUtils.setExpert(ticketWithMessages.expert?.email!!)
        val actualAttachment =
            chatService.getAttachment(ticketWithMessages.id!!, messageWithAttachment.id!!, expectedAttachment.id!!)
        assert(expectedAttachment.name == actualAttachment.name)
        assert(expectedAttachment.type == actualAttachment.type)
        assert(expectedAttachment.file.contentEquals(actualAttachment.file))
    }

    @Test
    @Transactional
    fun getAttachmentsManager() {
        val messageWithAttachment = ticketWithMessages.messages.toList()[messageWithAttachmentIndex]
        val expectedAttachment = messageWithAttachment.attachments.toList()[0]
        SecurityTestUtils.setManager()
        assertThrows<AccessDeniedException> {
            chatService.getAttachment(ticketWithMessages.id!!, messageWithAttachment.id!!, expectedAttachment.id!!)
        }
    }

}