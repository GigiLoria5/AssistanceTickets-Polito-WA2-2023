package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.ticket.NewTicketDTO
import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeDTO
import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeInProgressDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.exception.*
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketService
import it.polito.wa2.g29.server.service.TicketStatusChangeService
import it.polito.wa2.g29.server.utils.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
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

    lateinit var testProfiles: List<Profile>

    @BeforeAll
    fun prepare() {
        TicketTestUtils.products = ProductTestUtils.insertProducts(productRepository)
        testProfiles = ProfileTestUtils.insertProfiles(profileRepository)
        TicketTestUtils.profiles = testProfiles
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

        SecurityTestUtils.setClient(testTickets[0].customer.email)
        val actualTicketDTO = ticketService.getTicketById(expectedTicketDTO.ticketId!!)

        assert(actualTicketDTO == expectedTicketDTO)
    }

    @Test
    fun getTicketByIdNotFound() {
        SecurityTestUtils.setManager()
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
        SecurityTestUtils.setExpert(expert.email)
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
    fun getTicketStatusChangesByTicketIdOtherExpert() {
        val expert = TicketTestUtils.experts[0]
        val otherExpert = TicketTestUtils.experts[1]

        val ticketOne =
            Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[0]).apply {
                status = TicketStatus.IN_PROGRESS
                priorityLevel = TicketPriority.LOW
            }

        TicketTestUtils.addTicket(ticketRepository, expert, ticketOne)
        SecurityTestUtils.setExpert(expert.email)
        TicketTestUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expert,
            ticketOne,
            TicketStatus.RESOLVED,
            UserType.EXPERT,
            ""
        )

        SecurityTestUtils.setExpert(otherExpert.email)
        assertThrows<AccessDeniedException> {
            ticketService.getTicketStatusChangesByTicketId(ticketOne.id!!)
        }
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
        SecurityTestUtils.setExpert(expert.email)
        TicketTestUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expert,
            ticketOne,
            TicketStatus.RESOLVED,
            UserType.EXPERT,
            ""
        )
        SecurityTestUtils.setClient(ticketOne.customer.email)
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

        SecurityTestUtils.setClient(ticketOne.customer.email)
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
            productId = TicketTestUtils.products[1].id!!,
            title = "newtitle",
            description = "newdescription"
        )
        SecurityTestUtils.setClient(testProfiles[0].email)
        ticketService.createTicket(newTicketDTO)

        assert(ticketRepository.findAll().size == (testTickets.size + 1))
    }

    @Test
    @Transactional
    @Rollback
    fun createTicketProductNotFound() {
        val newTicketDTO = NewTicketDTO(
            productId = Int.MAX_VALUE,
            title = "newtitle",
            description = "newdescription"
        )
        SecurityTestUtils.setClient(testProfiles[0].email)

        assertThrows<ProductNotFoundException> {
            ticketService.createTicket(newTicketDTO)
        }
    }

    @Test
    @Transactional
    @Rollback
    fun createTicketDuplicateCustomerProduct() {
        val newTicketDTO = NewTicketDTO(
            productId = TicketTestUtils.products[1].id!!,
            title = "newtitle",
            description = "newdescription"
        )
        SecurityTestUtils.setClient(testProfiles[0].email)
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

        SecurityTestUtils.setClient(ticket.customer.email)
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
    fun ticketStatusChangeClient() {
        val ticket = testTickets[0]
        val expectedStatus = TicketStatus.RESOLVED

        SecurityTestUtils.setClient(ticket.customer.email)
        val statusChangeData = TicketStatusChangeDTO(
            description = null
        )

        ticketStatusChangeService.ticketStatusChange(ticket.id!!, expectedStatus, statusChangeData)

        val actualNewTicketDTO = ticketService.getTicketById(ticket.id!!)

        assert(actualNewTicketDTO.status == expectedStatus.toString())
    }

    @Test
    @Transactional
    @Rollback
    fun ticketStatusChangeOtherClient() {
        val ticket = testTickets[0]
        val expectedStatus = TicketStatus.RESOLVED

        SecurityTestUtils.setClient(testProfiles[1].email)
        val statusChangeData = TicketStatusChangeDTO(
            description = null
        )

        assertThrows<AccessDeniedException> {
            ticketStatusChangeService.ticketStatusChange(ticket.id!!, expectedStatus, statusChangeData)
        }
    }

    @Test
    @Transactional
    fun ticketStatusChangeNotFound() {
        val statusChangeData = TicketStatusChangeDTO(
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

        SecurityTestUtils.setClient(ticket.customer.email)
        val statusChangeData = TicketStatusChangeDTO(
            description = null
        )

        assertThrows<NotValidStatusChangeException> {
            ticketStatusChangeService.ticketStatusChange(ticket.id!!, newStatus, statusChangeData)
        }
    }
}