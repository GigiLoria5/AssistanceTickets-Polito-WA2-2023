package it.polito.wa2.g29.server.integration.repository

import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.utils.ProductTestUtils
import it.polito.wa2.g29.server.utils.ProfileTestUtils
import it.polito.wa2.g29.server.utils.TicketTestUtils
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketRepositoryIT : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var ticketRepository: TicketRepository

    lateinit var testTickets: List<Ticket>

    @BeforeAll
    fun prepare(
        @Autowired profileRepository: ProfileRepository,
        @Autowired productRepository: ProductRepository,
        @Autowired expertRepository: ExpertRepository
    ) {
        TicketTestUtils.products = ProductTestUtils.insertProducts(productRepository)
        TicketTestUtils.profiles = ProfileTestUtils.insertProfiles(profileRepository)
        testTickets = TicketTestUtils.insertTickets(ticketRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// findTicketsByStatus
    /////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class
    )
    @Transactional
    fun findTicketsByStatus(ticketStatus: TicketStatus) {
        val expectedTickets = testTickets.filter { it.status == ticketStatus }

        val actualTickets = ticketRepository.findTicketsByStatus(ticketStatus)

        assert(actualTickets.isNotEmpty() == expectedTickets.isNotEmpty())

        if (actualTickets.isNotEmpty()) {
            assert(actualTickets.size == expectedTickets.size)
            assert(actualTickets == expectedTickets)
        }
    }

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class
    )
    @Rollback
    fun findTicketsByStatusNotFound(ticketStatus: TicketStatus) {
        ticketRepository.deleteAllInBatch()

        val actualTickets = ticketRepository.findTicketsByStatus(ticketStatus)

        assert(actualTickets.isEmpty())
    }

    /////////////////////////////////////////////////////////////////////
    ////// findTicketByCustomerAndProductAndStatusNot
    /////////////////////////////////////////////////////////////////////

    @Test
    fun findTicketByCustomerAndProductAndStatusNot() {
        val expectedCustomer = TicketTestUtils.profiles[0]
        val expectedProduct = TicketTestUtils.products[0]
        val notExpectedStatus = TicketStatus.RESOLVED

        val expectedTicket =
            testTickets.find { it.customer == expectedCustomer && it.product == expectedProduct && it.status != notExpectedStatus }
        val actualTicket = ticketRepository.findTicketByCustomerAndProductAndStatusNot(
            expectedCustomer,
            expectedProduct,
            notExpectedStatus
        )

        assert(actualTicket != null)
        if (actualTicket != null) {
            assert(expectedTicket == actualTicket)
        }
    }

    @Test
    fun findNonExistingTicketByCustomerAndProductAndStatusNot() {
        val expectedCustomer = TicketTestUtils.profiles[1]
        val expectedProduct = TicketTestUtils.products[0]
        val notExpectedStatus = TicketStatus.OPEN

        val actualTicket = ticketRepository.findTicketByCustomerAndProductAndStatusNot(
            expectedCustomer,
            expectedProduct,
            notExpectedStatus
        )

        assert(actualTicket == null)
    }
}