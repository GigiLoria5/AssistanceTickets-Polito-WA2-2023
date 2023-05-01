package it.polito.wa2.g29.server.integration.controller

import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Ticket
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketControllerIntegrationTest : AbstractTestcontainersTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    lateinit var testTickets: List<Ticket>

    @BeforeAll
    fun prepare(@Autowired profileRepository: ProfileRepository, @Autowired productRepository: ProductRepository) {
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
    ////// GET /API/tickets
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getAllTicketsEmpty() {
        ticketRepository.deleteAllInBatch()
        mockMvc
            .perform(get("/API/tickets").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isEmpty
            )
    }

    @Test
    fun getAllTickets() {
        mockMvc
            .perform(get("/API/tickets").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isNotEmpty
            )
    }

}