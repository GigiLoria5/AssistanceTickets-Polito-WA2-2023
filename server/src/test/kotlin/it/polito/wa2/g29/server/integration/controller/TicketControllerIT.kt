package it.polito.wa2.g29.server.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeDTO
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.utils.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketControllerIT : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @Autowired
    private lateinit var profileRepository: ProfileRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var expertRepository: ExpertRepository

    lateinit var testProfiles: List<Profile>

    lateinit var testProducts: List<Product>

    lateinit var testExperts: List<Expert>

    @BeforeAll
    fun prepare() {
        testProducts = ProductTestUtils.insertProducts(productRepository)
        testProfiles = ProfileTestUtils.insertProfiles(profileRepository)
        testExperts = ExpertTestUtils.insertExperts(expertRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/tickets
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun getAllTicketsEmpty() {
        SecurityTestUtils.setManager()
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
    fun getAllTicketsClient() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        mockMvc
            .perform(get("/API/tickets").contentType("application/json"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun getAllTicketsExpert() {
        SecurityTestUtils.setExpert(testExperts[0].email)
        mockMvc
            .perform(get("/API/tickets").contentType("application/json"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun getAllTicketsWrongStatus() {
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/tickets").queryParam("status", "OPENED").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/tickets/{ticketId}
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getTicketByIdNotFound() {
        SecurityTestUtils.setManager()
        val ticketId = Int.MAX_VALUE
        mockMvc
            .perform(get("/API/tickets/${ticketId}").contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, -2, Int.MIN_VALUE]
    )
    fun getTicketInvalidId(id: Int) {
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/tickets/${id}").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun getTicketByIdWrongType() {
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/tickets/wrongType").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/tickets/{ticketId}/statusChanges
    /////////////////////////////////////////////////////////////////////


    @Test
    fun getTicketStatusChangesByIdNotFound() {
        val ticketId = Int.MAX_VALUE
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/tickets/${ticketId}/statusChanges").contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, -2, Int.MIN_VALUE]
    )
    fun getTicketStatusChangesInvalidId(id: Int) {
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/tickets/${id}/statusChanges").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }


    /////////////////////////////////////////////////////////////////////
    ////// PUT /API/tickets/{ticketId}/stop
    /////////////////////////////////////////////////////////////////////


    @Test
    fun stopTicketByIdNotFound() {
        SecurityTestUtils.setManager()
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            description = null
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${Int.MAX_VALUE}/stop")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound)
    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, -2, Int.MIN_VALUE]
    )
    fun stopTicketByInvalidId(id: Int) {
        SecurityTestUtils.setManager()
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            description = null
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${id}/stop")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
    }


    /////////////////////////////////////////////////////////////////////
    ////// PUT /API/tickets/{ticketId}/resolve
    /////////////////////////////////////////////////////////////////////


    @Test
    fun resolveTicketByIdNotFound() {
        SecurityTestUtils.setManager()
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            description = null
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${Int.MAX_VALUE}/resolve")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound)
    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, -2, Int.MIN_VALUE]
    )
    fun resolveTicketByInvalidId(id: Int) {
        SecurityTestUtils.setExpert(testExperts[0].email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            description = null
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${id}/resolve")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
    }


    /////////////////////////////////////////////////////////////////////
    ////// PUT /API/tickets/{ticketId}/reopen
    /////////////////////////////////////////////////////////////////////


    @Test
    fun reopenTicketByIdNotFound() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            description = "description"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${Int.MAX_VALUE}/reopen")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound)
    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, -2, Int.MIN_VALUE]
    )
    fun reopenTicketInvalidId(id: Int) {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            description = "description"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${id}/reopen")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
    }


    /////////////////////////////////////////////////////////////////////
    ////// PUT /API/tickets/{ticketId}/close
    /////////////////////////////////////////////////////////////////////


    @Test
    fun closeTicketByIdNotFound() {
        SecurityTestUtils.setManager()
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            description = null
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${Int.MAX_VALUE}/close")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound)
    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, -2, Int.MIN_VALUE]
    )
    fun closeTicketInvalidId(id: Int) {
        SecurityTestUtils.setManager()
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            description = null
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${id}/close")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
    }

}
