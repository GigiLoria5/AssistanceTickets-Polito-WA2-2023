package it.polito.wa2.g29.server.integration.repository

import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.utils.ProductTestUtils
import it.polito.wa2.g29.server.utils.ProfileTestUtils
import it.polito.wa2.g29.server.utils.TicketTestUtils
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketRepositoryIT : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @BeforeAll
    fun prepare(
        @Autowired profileRepository: ProfileRepository,
        @Autowired productRepository: ProductRepository,
        @Autowired expertRepository: ExpertRepository
    ) {
        TicketTestUtils.products = ProductTestUtils.insertProducts(productRepository)
        TicketTestUtils.profiles = ProfileTestUtils.insertProfiles(profileRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// findTicketsByStatus
    /////////////////////////////////////////////////////////////////////


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
}
