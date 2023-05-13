package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.ticket.NewTicketDTO
import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeDTO
import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeInProgressDTO
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
import it.polito.wa2.g29.server.utils.ExpertTestUtils
import it.polito.wa2.g29.server.utils.ProductTestUtils
import it.polito.wa2.g29.server.utils.ProfileTestUtils
import it.polito.wa2.g29.server.utils.TicketTestUtils
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketServiceIT : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var ticketService: TicketService

    @Autowired
    private lateinit var ticketStatusChangeService: TicketStatusChangeService

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @Autowired
    private lateinit var profileRepository: ProfileRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var expertRepository: ExpertRepository

    lateinit var testTickets: List<Ticket>

    @BeforeAll
    fun prepare() {
        TicketTestUtils.products = ProductTestUtils.insertProducts(productRepository)
        TicketTestUtils.profiles = ProfileTestUtils.insertProfiles(profileRepository)
        TicketTestUtils.experts = ExpertTestUtils.insertExperts(expertRepository)
        testTickets = TicketTestUtils.insertTickets(ticketRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// getAllTickets
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun getAllTicketsEmpty() {
        ticketRepository.deleteAll()

        val tickets = ticketService.getAllTickets()

        assert(tickets.isEmpty())
    }

    @Test
    @Transactional
    fun getAllTickets() {
        val expectedTickets = testTickets

        val actualTickets = ticketService.getAllTickets()

        assert(actualTickets.isNotEmpty())
        assert(actualTickets.size == expectedTickets.size)
        expectedTickets.forEach {
            actualTickets.contains(it.toDTO())
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// getTicketsByStatus
    /////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class
    )
    @Transactional
    fun getTicketsByStatus(ticketStatus: TicketStatus) {
        val expectedTicketsDTO = testTickets.filter { it.status == ticketStatus }.map { it.toDTO() }

        val actualTicketsDTO = ticketService.getTicketsByStatus(ticketStatus)

        assert(actualTicketsDTO.isNotEmpty() == expectedTicketsDTO.isNotEmpty())
        assert(actualTicketsDTO.size == expectedTicketsDTO.size)
        expectedTicketsDTO.forEach {
            actualTicketsDTO.contains(it)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// getTicketById
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
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
    @Rollback
    fun getTicketStatusChangesByTicketId() {
        val expert = TicketTestUtils.experts[0]

        val ticketOne =
            Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[0]).apply {
                status = TicketStatus.IN_PROGRESS
                priorityLevel = TicketPriority.LOW
            }

        TicketTestUtils.addTicket(ticketRepository, expert, ticketOne)
        TicketTestUtils.addTicketStatusChange(
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
    @Rollback
    fun getTicketStatusChangesByTicketIdWithManyChanges() {
        val expert = TicketTestUtils.experts[0]

        val ticketOne =
            Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[0]).apply {
                status = TicketStatus.IN_PROGRESS
                priorityLevel = TicketPriority.LOW
            }

        TicketTestUtils.addTicket(ticketRepository, expert, ticketOne)
        TicketTestUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expert,
            ticketOne,
            TicketStatus.RESOLVED,
            UserType.EXPERT,
            ""
        )
        TicketTestUtils.addTicketStatusChange(
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
            ticketOneActualTicketsStatusChangesDTO[i].time >= ticketOneActualTicketsStatusChangesDTO[i + 1].time
        }
    }

    @Test
    @Transactional
    @Rollback
    fun getTicketStatusChangesByTicketIdWithoutChanges() {
        val expert = TicketTestUtils.experts[0]

        val ticketOne =
            Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[0]).apply {
                status = TicketStatus.IN_PROGRESS
                priorityLevel = TicketPriority.LOW
            }

        TicketTestUtils.addTicket(ticketRepository, expert, ticketOne)

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
    @Transactional
    @Rollback
    fun createTicket() {
        val newTicketDTO = NewTicketDTO(
            customerId = TicketTestUtils.profiles[0].id!!,
            productId = TicketTestUtils.products[1].id!!,
            title = "newtitle",
            description = "newdescription"
        )

        ticketService.createTicket(newTicketDTO)

        assert(ticketRepository.findAll().size == (testTickets.size + 1))
    }

    @Test
    @Transactional
    @Rollback
    fun createTicketDuplicateCustomerProduct() {
        val newTicketDTO = NewTicketDTO(
            customerId = TicketTestUtils.profiles[0].id!!,
            productId = TicketTestUtils.products[1].id!!,
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

    @Test
    @Transactional
    @Rollback
    fun ticketStatusChangeInProgress() {
        val ticket = testTickets[0]
        val expectedStatus = TicketStatus.IN_PROGRESS

        val statusChangeData = TicketStatusChangeInProgressDTO(
            expertId = TicketTestUtils.experts[0].id!!,
            priorityLevel = TicketPriority.LOW,
            description = null
        )
        ticketStatusChangeService.ticketStatusChangeInProgress(ticket.id!!, statusChangeData)

        val actualNewTicketDTO = ticketService.getTicketById(ticket.id!!)

        assert(actualNewTicketDTO.status == expectedStatus.toString())
    }

    @Test
    @Transactional
    @Rollback
    fun ticketStatusChangeInProgressTicketNotFound() {
        val statusChangeData = TicketStatusChangeInProgressDTO(
            expertId = TicketTestUtils.experts[0].id!!,
            priorityLevel = TicketPriority.LOW,
            description = null
        )

        assertThrows<TicketNotFoundException> {
            ticketStatusChangeService.ticketStatusChangeInProgress(Int.MAX_VALUE, statusChangeData)
        }
    }

    @Test
    @Transactional
    @Rollback
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

    @Test
    @Transactional
    @Rollback
    fun ticketStatusChange() {
        val ticket = testTickets[0]
        val expectedStatus = TicketStatus.RESOLVED

        val statusChangeData = TicketStatusChangeDTO(
            changedBy = UserType.CUSTOMER,
            description = null
        )

        ticketStatusChangeService.ticketStatusChange(ticket.id!!, expectedStatus, statusChangeData)

        val actualNewTicketDTO = ticketService.getTicketById(ticket.id!!)

        assert(actualNewTicketDTO.status == expectedStatus.toString())
    }

    @Test
    @Transactional
    fun ticketStatusChangeNotFound() {
        val statusChangeData = TicketStatusChangeDTO(
            changedBy = UserType.CUSTOMER,
            description = null
        )

        assertThrows<TicketNotFoundException> {
            ticketStatusChangeService.ticketStatusChange(Int.MAX_VALUE, TicketStatus.RESOLVED, statusChangeData)
        }
    }

    @Test
    @Transactional
    fun ticketStatusChangeNotAllowed() {
        val ticket = testTickets[0]
        val newStatus = TicketStatus.REOPENED

        val statusChangeData = TicketStatusChangeDTO(
            changedBy = UserType.CUSTOMER,
            description = null
        )

        assertThrows<NotValidStatusChangeException> {
            ticketStatusChangeService.ticketStatusChange(ticket.id!!, newStatus, statusChangeData)
        }
    }
}