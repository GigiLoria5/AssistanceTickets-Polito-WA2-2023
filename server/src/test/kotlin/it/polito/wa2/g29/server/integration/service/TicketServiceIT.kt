package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeDTO
import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeInProgressDTO
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.exception.*
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketService
import it.polito.wa2.g29.server.service.TicketStatusChangeService
import it.polito.wa2.g29.server.utils.*
import org.junit.jupiter.api.*
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

    lateinit var testProfiles: List<Profile>

    @BeforeAll
    fun prepare() {
        TicketTestUtils.products = ProductTestUtils.insertProducts(productRepository)
        testProfiles = ProfileTestUtils.insertProfiles(profileRepository)
        TicketTestUtils.profiles = testProfiles
        TicketTestUtils.experts = ExpertTestUtils.insertExperts(expertRepository)
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


    /////////////////////////////////////////////////////////////////////
    ////// getTicketById
    /////////////////////////////////////////////////////////////////////


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
    fun getTicketStatusChangesByTicketIdNotFound() {
        val ticketId = Int.MAX_VALUE

        assertThrows<TicketNotFoundException> {
            ticketService.getTicketStatusChangesByTicketId(ticketId)
        }
    }


    /////////////////////////////////////////////////////////////////////
    ////// ticketStatusChangeInProgress
    /////////////////////////////////////////////////////////////////////


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


    /////////////////////////////////////////////////////////////////////
    ////// ticketStatusChange
    /////////////////////////////////////////////////////////////////////


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
}
