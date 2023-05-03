package it.polito.wa2.g29.server.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.utils.TestExpertUtils
import it.polito.wa2.g29.server.utils.TestProductUtils
import it.polito.wa2.g29.server.utils.TestProfileUtils
import it.polito.wa2.g29.server.utils.TestTicketUtils
import org.hamcrest.Matchers.*
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
                jsonPath("$").isNotEmpty,
                jsonPath("$[*].ticketId").exists(),
                jsonPath("$[*].title").exists(),
                jsonPath("$[*].description").exists(),
                jsonPath("$[*].productId").exists(),
                jsonPath("$[*].customerId").exists(),
                jsonPath("$[*].expertId").exists(),
                jsonPath("$[*].totalExchangedMessages").exists(),
                jsonPath("$[*].status").exists(),
                jsonPath("$[*].priorityLevel").exists(),
                jsonPath("$[*].createdAt").exists(),
                jsonPath("$[*].lastModifiedAt").exists()
                )
    }

    @Test
    fun getAllTicketsOpen() {
        mockMvc
            .perform(get("/API/tickets").queryParam("status", "OPEN").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isNotEmpty,
                jsonPath("$[*].ticketId").exists(),
                jsonPath("$[*].title").exists(),
                jsonPath("$[*].description").exists(),
                jsonPath("$[*].productId").exists(),
                jsonPath("$[*].customerId").exists(),
                jsonPath("$[*].expertId").exists(),
                jsonPath("$[*].totalExchangedMessages").exists(),
                jsonPath("$[*].status").exists(),
                jsonPath("$[*].priorityLevel").exists(),
                jsonPath("$[*].createdAt").exists(),
                jsonPath("$[*].lastModifiedAt").exists(),
                jsonPath("$[*].status").value(List(2){"OPEN"})
            // TODO: generalize for all sizes and all status
            )
    }

    @Test
    fun getAllTicketsWrongStatus() {
        mockMvc
            .perform(get("/API/tickets").queryParam("status", "OPENED").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/tickets/{ticketId}
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getTicketById() {
        val ticket = testTickets[0]
        mockMvc
            .perform(get("/API/tickets/${ticket.id}").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.ticketId").value(ticket.id),
                jsonPath("$.title").value(ticket.title),
                jsonPath("$.description").value(ticket.description),
                jsonPath("$.productId").value(ticket.product.id),
                jsonPath("$.customerId").value(ticket.customer.id),
                jsonPath("$.expertId").value(null),
                jsonPath("$.totalExchangedMessages").value(ticket.messages.size),
                jsonPath("$.status").value(ticket.status.toString()),
                jsonPath("$.priorityLevel").value(ticket.priorityLevel),
                jsonPath("$.createdAt").value(ticket.createdAt),
                jsonPath("$.lastModifiedAt").value(ticket.lastModifiedAt)
            )
    }

    @Test
    fun getTicketByIdNotFound() {
        val ticketId = Int.MAX_VALUE
        mockMvc
            .perform(get("/API/tickets/${ticketId}").contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun getTicketByIdWrongValue() {
        val ticketIdWrongValues = listOf(0, -1, -2, Int.MIN_VALUE)

        ticketIdWrongValues.forEach{
            mockMvc
                .perform(get("/API/tickets/${it}").contentType("application/json"))
                .andExpect(status().isUnprocessableEntity)
        }
    }

    @Test
    fun getTicketByIdWrongType() {
        mockMvc
            .perform(get("/API/tickets/wrongType").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/tickets/{ticketId}/statusChanges
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getTicketStatusChangesById() {
        val ticket = testTickets[0]
        mockMvc
            .perform(get("/API/tickets/${ticket.id}/statusChanges").contentType("application/json"))
            .andExpect(status().isOk)
        // TODO: to complete
    }

    /////////////////////////////////////////////////////////////////////
    ////// POST /API/tickets
    /////////////////////////////////////////////////////////////////////

    @Test
    fun createTicket() {
        val newTicketDTO = TestTicketUtils.getNewTicketDTO()

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(post("/API/tickets")
                .content(jsonBody)
                .contentType("application/json"))
            .andExpect(status().isCreated)
            .andExpect { jsonPath("$.ticketId").exists() }
    }

    // TODO: all right cases

    @Test
    fun createTicketCustomerNotFound() {
        val newTicketDTO = TestTicketUtils.getNewTicketDTO().copy(
            customerId = Int.MAX_VALUE
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(post("/API/tickets")
                .content(jsonBody)
                .contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun createTicketProductNotFound() {
        val newTicketDTO = TestTicketUtils.getNewTicketDTO().copy(
            productId = Int.MAX_VALUE
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(post("/API/tickets")
                .content(jsonBody)
                .contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun createTicketDuplicateCustomerProduct() {
        val newTicketDTO = testTickets[0].toDTO().copy()

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(post("/API/tickets")
                .content(jsonBody)
                .contentType("application/json"))
            .andExpect(status().isConflict)
    }

    @Test
    fun createTicketBlankFields() {
        val newTicketDTO = TestTicketUtils.getNewTicketDTO().copy(
            title = "",
            description = ""
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(post("/API/tickets")
                .content(jsonBody)
                .contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.error").exists() }
    }

    @Test
    fun createTicketFieldsWithSpaces() {
        val newTicketDTO = TestTicketUtils.getNewTicketDTO().copy(
            title = "   ",
            description = " "
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(post("/API/tickets")
                .content(jsonBody)
                .contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.error").exists() }
    }

    @Test
    fun createTicketInvalidCustomerIdOrProductId() {
        val newTicketDTO = TestTicketUtils.getNewTicketDTO().copy(
            customerId = -1,
            productId = -1
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(post("/API/tickets")
                .content(jsonBody)
                .contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.error").exists() }
    }

    /////////////////////////////////////////////////////////////////////
    ////// PUT /API/tickets/{ticketId}/start
    /////////////////////////////////////////////////////////////////////


    @Test
    fun startTicketById() {
        val oldTicketDTO = testTickets[0].toDTO()
        val newTicketDTO = oldTicketDTO.copy(
            expertId = TestTicketUtils.experts[0].id,
            priorityLevel = "LOW",
            description = ""
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(
                put("/API/tickets/${oldTicketDTO.ticketId}/start")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun startTicketByIdNotFound() {
        val oldTicketDTO = testTickets[0].toDTO()
        val newTicketDTO = oldTicketDTO.copy(
            expertId = 1,
            priorityLevel = "LOW",
            description = ""
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(
                put("/API/tickets/${Int.MAX_VALUE}/start")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound)
    }

    @Test
    fun startTicketByExpertIdNotFound() {
        val oldTicketDTO = testTickets[0].toDTO()
        val newTicketDTO = oldTicketDTO.copy(
            expertId = Int.MAX_VALUE,
            priorityLevel = "LOW",
            description = ""
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(
                put("/API/tickets/${oldTicketDTO.ticketId}/start")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound)
    }

    @Test
    fun startTicketByIdInProgress() {
        val expert = TestTicketUtils.experts[0]

        val ticket = Ticket("title1", "description1", TestTicketUtils.products[0], TestTicketUtils.profiles[1]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }

        TestExpertUtils.addTicket(ticketRepository, expert, ticket)

        val newTicketDTO = ticket.toDTO().copy(
            expertId = expert.id,
            priorityLevel = "LOW",
            description = ""
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(
                put("/API/tickets/${ticket.toDTO().ticketId}/start")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
    }

}