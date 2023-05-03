package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.ticketDTOs.NewTicketDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.exception.DuplicateTicketException
import it.polito.wa2.g29.server.exception.TicketNotFoundException
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketService
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
    private lateinit var ticketRepository: TicketRepository

    lateinit var testTickets: List<Ticket>

    @BeforeAll
    fun prepare(@Autowired profileRepository: ProfileRepository, @Autowired productRepository: ProductRepository, @Autowired expertRepository: ExpertRepository) {
        productRepository.deleteAll()
        profileRepository.deleteAll()
        TestTicketUtils.products = TestProductUtils.insertProducts(productRepository)
        TestTicketUtils.profiles = TestProfileUtils.insertProfiles(profileRepository)
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

    // TODO: getTicketStatusChangesByTicketId

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
}