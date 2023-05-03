package it.polito.wa2.g29.server.integration.repository

import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.utils.TestProductUtils
import it.polito.wa2.g29.server.utils.TestProfileUtils
import it.polito.wa2.g29.server.utils.TestTicketUtils
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketRepositoryIntegrationTest: AbstractTestcontainersTest() {

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    lateinit var testTickets: List<Ticket>

    @BeforeAll
    fun prepare(@Autowired profileRepository: ProfileRepository, @Autowired productRepository: ProductRepository, @Autowired expertRepository: ExpertRepository) {
        productRepository.deleteAllInBatch()
        profileRepository.deleteAllInBatch()
        TestTicketUtils.products = TestProductUtils.insertProducts(productRepository)
        TestTicketUtils.profiles = TestProfileUtils.insertProfiles(profileRepository)
    }

    @BeforeEach
    fun setup() {
        ticketRepository.deleteAllInBatch()
        testTickets = TestTicketUtils.insertTickets(ticketRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// findTicketsByStatus
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    fun findTicketsByStatus() {
        val expectedStatus = TicketStatus.OPEN
        val expectedTickets = testTickets.filter { it.status == expectedStatus }

        val actualTickets = ticketRepository.findTicketsByStatus(expectedStatus)

        assert(actualTickets.isNotEmpty())

        if (actualTickets.isNotEmpty()) {
            assert(actualTickets.size == expectedTickets.size)
            assert(actualTickets == expectedTickets)
        }
    }

    @Test
    fun findTicketsByStatusNotFound() {
        val expectedStatus = TicketStatus.IN_PROGRESS
        val actualTickets = ticketRepository.findTicketsByStatus(expectedStatus)

        assert(actualTickets.isEmpty())
    }

    /////////////////////////////////////////////////////////////////////
    ////// findTicketByCustomerAndProductAndStatusNot
    /////////////////////////////////////////////////////////////////////

    @Test
    fun findTicketByCustomerAndProductAndStatusNot() {
        val expectedCustomer = TestTicketUtils.profiles[0]
        val expectedProduct = TestTicketUtils.products[0]
        val notExpectedStatus = TicketStatus.RESOLVED

        val expectedTicket = testTickets.find { it.customer == expectedCustomer && it.product == expectedProduct && it.status != notExpectedStatus }
        val actualTicket = ticketRepository.findTicketByCustomerAndProductAndStatusNot(expectedCustomer, expectedProduct, notExpectedStatus)

        assert(actualTicket != null)
        if (actualTicket != null) {
            assert(expectedTicket == actualTicket)
        }
    }

    @Test
    fun findNonExistingTicketByCustomerAndProductAndStatusNot() {
        val expectedCustomer = TestTicketUtils.profiles[1]
        val expectedProduct = TestTicketUtils.products[0]
        val notExpectedStatus = TicketStatus.OPEN

        val actualTicket = ticketRepository.findTicketByCustomerAndProductAndStatusNot(expectedCustomer, expectedProduct, notExpectedStatus)

        assert(actualTicket == null)
    }
}