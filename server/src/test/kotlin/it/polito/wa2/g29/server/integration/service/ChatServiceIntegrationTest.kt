package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.enums.AttachmentType
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.exception.TicketNotFoundException
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.*
import it.polito.wa2.g29.server.repository.*
import it.polito.wa2.g29.server.service.ChatService
import it.polito.wa2.g29.server.service.TicketStatusChangeService
import it.polito.wa2.g29.server.utils.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatServiceIntegrationTest : AbstractTestcontainersTest() {
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

    @BeforeEach
    fun setup() {
        TestChatUtils.deleteAllMessages(messageRepository, testTickets, testExperts)
        ticketRepository.deleteAllInBatch()
        testTickets = TestTicketUtils.insertTickets(ticketRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// getMessagesByTicketId
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    fun getMessagesByTicketIdWithNoMessages() {
        val ticket = testTickets[0]

        val actualMessageDTOs = chatService.getMessagesByTicketId(ticket.id!!)

        assert(actualMessageDTOs.isEmpty())
    }

    @Test
    @Transactional
    fun getMessagesByTicketIdWithManyMessages() {
        val ticket = testTickets[0]
        val ticketExpert = testExperts[0]
        ticket.apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }
        TestExpertUtils.addTicket(ticketRepository, ticketExpert, ticket)
        val messages = TestChatUtils.createMessages(ticket, ticketExpert)
        messages[0].apply {
            attachments = setOf(
                Attachment(
                    "AttachmentName",
                    byteArrayOf(0x00, 0x01, 0x02),
                    AttachmentType.OTHER,
                    this
                )
            )
        }
        TestChatUtils.addMessages(messageRepository, messages, ticket, ticketExpert)
        val expectedMessageDTOs = messages.map { it.toDTO() }

        val actualMessageDTOs = chatService.getMessagesByTicketId(ticket.id!!)

        assert(actualMessageDTOs.isNotEmpty())
        assert(expectedMessageDTOs.size == actualMessageDTOs.size)
        assert(expectedMessageDTOs == actualMessageDTOs)
    }

    @Test
    fun getMessagesByTicketIdNotFound() {
        assertThrows<TicketNotFoundException> {
            chatService.getMessagesByTicketId(Int.MAX_VALUE)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// addMessageWithAttachments
    /////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////
    ////// getAttachments
    /////////////////////////////////////////////////////////////////////

}