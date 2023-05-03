package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.ticketDTOs.NewTicketDTO
import it.polito.wa2.g29.server.dto.ticketDTOs.TicketStatusChangeDTO
import it.polito.wa2.g29.server.dto.ticketDTOs.TicketStatusChangeInProgressDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.exception.DuplicateTicketException
import it.polito.wa2.g29.server.exception.ExpertNotFoundException
import it.polito.wa2.g29.server.exception.NotValidStatusChangeException
import it.polito.wa2.g29.server.exception.TicketNotFoundException
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketService
import it.polito.wa2.g29.server.service.TicketStatusChangeService
import it.polito.wa2.g29.server.utils.TestExpertUtils
import it.polito.wa2.g29.server.utils.TestProductUtils
import it.polito.wa2.g29.server.utils.TestProfileUtils
import it.polito.wa2.g29.server.utils.TestTicketUtils
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketServiceIntegrationTest: AbstractTestcontainersTest() {

    @Autowired
    private lateinit var ticketService: TicketService

    @Autowired
    private lateinit var ticketStatusChangeService: TicketStatusChangeService


    @Autowired
    private lateinit var ticketRepository: TicketRepository

    lateinit var testTickets: List<Ticket>

    @BeforeAll
    fun prepare(@Autowired profileRepository: ProfileRepository, @Autowired productRepository: ProductRepository, @Autowired expertRepository: ExpertRepository) {
        productRepository.deleteAll()
        profileRepository.deleteAll()
        expertRepository.deleteAll()
        TestTicketUtils.products = TestProductUtils.insertProducts(productRepository)
        TestTicketUtils.profiles = TestProfileUtils.insertProfiles(profileRepository)
        TestTicketUtils.experts = TestExpertUtils.insertExperts(expertRepository)
    }

    @BeforeEach
    fun setup() {
        ticketRepository.deleteAll()
        testTickets = TestTicketUtils.insertTickets(ticketRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// getAllTickets
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getAllTicketsEmpty() {
        ticketRepository.deleteAll()

        val tickets = ticketService.getAllTickets()

        assert(tickets.isEmpty())
    }

    @Transactional
    @Test
    fun getAllTickets() {
        val expectedTickets = testTickets

        val actualTickets = ticketService.getAllTickets()

        assert(actualTickets.isNotEmpty())
        assert(actualTickets.size == expectedTickets.size)
        expectedTickets.forEach{
            actualTickets.contains(it.toDTO())
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// getTicketsByStatus
    /////////////////////////////////////////////////////////////////////

    @Transactional
    @Test
    fun getTicketsByStatus() {
        val expectedStatus = TicketStatus.OPEN
        val expectedTicketsDTO = testTickets.filter { it.status == expectedStatus }.map { it.toDTO() }

        val actualTicketsDTO = ticketService.getTicketsByStatus(expectedStatus)

        assert(actualTicketsDTO.isNotEmpty())
        assert(actualTicketsDTO.size == expectedTicketsDTO.size)
        expectedTicketsDTO.forEach {
            actualTicketsDTO.contains(it)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// getTicketById
    /////////////////////////////////////////////////////////////////////

    @Transactional
    @Test
    fun getTicketById() {
        val expectedTicketDTO = testTickets[0].toDTO()

        val actualTicketDTO = ticketService.getTicketById(expectedTicketDTO.ticketId!!)

        assert(actualTicketDTO == expectedTicketDTO)
    }

    @Test
    fun getTicketByIdNotFound() {
        assertThrows<TicketNotFoundException> {
            ticketService.getTicketById(Int.MAX_VALUE)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// getTicketStatusChangesByTicketId
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    fun getTicketStatusChangesByTicketId() {
        val expert = TestTicketUtils.experts[0]

        val ticketOne = Ticket("title1", "description1", TestTicketUtils.products[0], TestTicketUtils.profiles[0]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }

        TestTicketUtils.addTicket(ticketRepository, expert, ticketOne)
        TestTicketUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expert,
            ticketOne,
            TicketStatus.RESOLVED,
            UserType.EXPERT,
            ""
        )

        val ticketOneActualTicketsStatusChangesDTO = ticketService.getTicketStatusChangesByTicketId(ticketOne.id!!)

        assert(ticketOneActualTicketsStatusChangesDTO.isNotEmpty())
        assert(ticketOneActualTicketsStatusChangesDTO.size == 1)
    }

    @Test
    @Transactional
    fun getTicketStatusChangesByTicketIdWithManyChanges() {
        val expert = TestTicketUtils.experts[0]

        val ticketOne = Ticket("title1", "description1", TestTicketUtils.products[0], TestTicketUtils.profiles[0]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }

        TestTicketUtils.addTicket(ticketRepository, expert, ticketOne)
        TestTicketUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expert,
            ticketOne,
            TicketStatus.RESOLVED,
            UserType.EXPERT,
            ""
        )
        TestTicketUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expert,
            ticketOne,
            TicketStatus.CLOSED,
            UserType.CUSTOMER,
            "It works now"
        )

        val ticketOneActualTicketsStatusChangesDTO = ticketService.getTicketStatusChangesByTicketId(ticketOne.id!!)

        assert(ticketOneActualTicketsStatusChangesDTO.isNotEmpty())
        assert(ticketOneActualTicketsStatusChangesDTO.size == 2)

        for (i in 0 until ticketOneActualTicketsStatusChangesDTO.size - 1) {
            ticketOneActualTicketsStatusChangesDTO[i].time >= ticketOneActualTicketsStatusChangesDTO[i+1].time
        }
    }

    @Test
    @Transactional
    fun getTicketStatusChangesByTicketIdWithoutChanges() {
        val expert = TestTicketUtils.experts[0]

        val ticketOne = Ticket("title1", "description1", TestTicketUtils.products[0], TestTicketUtils.profiles[0]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }

        TestTicketUtils.addTicket(ticketRepository, expert, ticketOne)

        val ticketOneActualTicketsStatusChangesDTO = ticketService.getTicketStatusChangesByTicketId(ticketOne.id!!)

        assert(ticketOneActualTicketsStatusChangesDTO.isEmpty())
    }

    @Test
    fun getTicketStatusChangesByTicketIdNotFound() {
        val ticketId = Int.MAX_VALUE

        assertThrows<TicketNotFoundException> {
            ticketService.getTicketStatusChangesByTicketId(ticketId)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// createTicket
    /////////////////////////////////////////////////////////////////////

    @Test
    fun createTicket() {
        val newTicketDTO = NewTicketDTO(
            customerId = TestTicketUtils.profiles[0].id!!,
            productId = TestTicketUtils.products[1].id!!,
            title = "newtitle",
            description = "newdescription"
        )

        ticketService.createTicket(newTicketDTO)

        assert(ticketRepository.findAll().size == (testTickets.size + 1))
    }

    @Test
    fun createTicketDuplicateCustomerProduct() {
        val newTicketDTO = NewTicketDTO(
            customerId = TestTicketUtils.profiles[0].id!!,
            productId = TestTicketUtils.products[1].id!!,
            title = "newtitle",
            description = "newdescription"
        )

        val duplicateTicketDTO = newTicketDTO.copy()
        ticketService.createTicket(newTicketDTO)

        assertThrows<DuplicateTicketException> {
            ticketService.createTicket(duplicateTicketDTO)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// ticketStatusChangeInProgress
    /////////////////////////////////////////////////////////////////////

    @Transactional
    @Test
    fun ticketStatusChangeInProgress() {
        val ticket = testTickets[0]
        val expectedStatus = TicketStatus.IN_PROGRESS

        val statusChangeData = TicketStatusChangeInProgressDTO(
            expertId = TestTicketUtils.experts[0].id!!,
            priorityLevel = TicketPriority.LOW,
            description = null
        )
        ticketStatusChangeService.ticketStatusChangeInProgress(ticket.id!!, statusChangeData)

        val actualNewTicketDTO = ticketService.getTicketById(ticket.id!!)

        assert(actualNewTicketDTO.status == expectedStatus.toString())
    }

    @Transactional
    @Test
    fun ticketStatusChangeInProgressTicketNotFound() {

        val statusChangeData = TicketStatusChangeInProgressDTO(
            expertId = TestTicketUtils.experts[0].id!!,
            priorityLevel = TicketPriority.LOW,
            description = null
        )

        assertThrows<TicketNotFoundException> {
            ticketStatusChangeService.ticketStatusChangeInProgress(Int.MAX_VALUE, statusChangeData)
        }
    }

    @Transactional
    @Test
    fun ticketStatusChangeInProgressExpertNotFound() {
        val ticket = testTickets[0]

        val statusChangeData = TicketStatusChangeInProgressDTO(
            expertId = Int.MAX_VALUE,
            priorityLevel = TicketPriority.LOW,
            description = null
        )

        assertThrows<ExpertNotFoundException> {
            ticketStatusChangeService.ticketStatusChangeInProgress(ticket.id!!, statusChangeData)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// ticketStatusChange
    /////////////////////////////////////////////////////////////////////

    @Transactional
    @Test
    fun ticketStatusChange() {
        val ticket = testTickets[0]
        val expectedStatus = TicketStatus.RESOLVED

        val statusChangeData = TicketStatusChangeDTO(
            changedBy = UserType.CUSTOMER,
            description = null
        )

        ticketStatusChangeService.ticketStatusChange(ticket.id!!, expectedStatus, statusChangeData )

        val actualNewTicketDTO = ticketService.getTicketById(ticket.id!!)

        assert(actualNewTicketDTO.status == expectedStatus.toString())
    }

    @Transactional
    @Test
    fun ticketStatusChangeNotFound() {

        val statusChangeData = TicketStatusChangeDTO(
            changedBy = UserType.CUSTOMER,
            description = null
        )

        assertThrows<TicketNotFoundException> {
            ticketStatusChangeService.ticketStatusChange(Int.MAX_VALUE, TicketStatus.RESOLVED, statusChangeData )
        }
    }

    @Transactional
    @Test
    fun ticketStatusChangeNotAllowed() {
        val ticket = testTickets[0]
        val newStatus = TicketStatus.REOPENED

        val statusChangeData = TicketStatusChangeDTO(
            changedBy = UserType.CUSTOMER,
            description = null
        )

        assertThrows<NotValidStatusChangeException> {
            ticketStatusChangeService.ticketStatusChange(ticket.id!!, newStatus, statusChangeData )
        }
    }
}