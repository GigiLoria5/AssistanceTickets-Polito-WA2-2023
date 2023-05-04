package it.polito.wa2.g29.server.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketStatusChangeService
import it.polito.wa2.g29.server.utils.ExpertTestUtils
import it.polito.wa2.g29.server.utils.ProductTestUtils
import it.polito.wa2.g29.server.utils.ProfileTestUtils
import it.polito.wa2.g29.server.utils.TicketTestUtils
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.transaction.annotation.Transactional

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketControllerIT : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

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
    ////// GET /API/tickets
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
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
                jsonPath("$[*].status").value(List(2) { "OPEN" })
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

        ticketIdWrongValues.forEach {
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
    @Transactional
    @Rollback
    fun getTicketStatusChangesById() {
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


        mockMvc
            .perform(get("/API/tickets/${ticketOne.id}/statusChanges").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isNotEmpty,
                jsonPath<List<TicketChangeDTO>>("$", hasSize(2)),
                jsonPath("$[*].ticketId").value(List(2) { ticketOne.id }),
                jsonPath("$[*].currentExpertId").value(contains(expert.id, expert.id)),
                jsonPath("$[*].oldStatus").exists(),
                jsonPath("$[*].newStatus").exists(),
                jsonPath("$[*].changedBy").exists(),
                jsonPath("$[*].description").exists(),
                jsonPath("$[*].time").exists()
            )
    }

    @Test
    @Transactional
    @Rollback
    fun getTicketStatusChangesByIdWithNoChanges() {
        val expert = TicketTestUtils.experts[0]

        val ticketOne =
            Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[0]).apply {
                status = TicketStatus.IN_PROGRESS
                priorityLevel = TicketPriority.LOW
            }

        TicketTestUtils.addTicket(ticketRepository, expert, ticketOne)

        mockMvc
            .perform(get("/API/tickets/${ticketOne.id}/statusChanges").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isEmpty
            )
    }

    @Test
    fun getTicketStatusChangesByIdNotFound() {
        val ticketId = Int.MAX_VALUE

        mockMvc
            .perform(get("/API/tickets/${ticketId}/statusChanges").contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun getTicketStatusChangesByIdWrongIdType() {
        val ticketIdWrongValues = listOf("wrong", 0, -1, Int.MIN_VALUE)

        ticketIdWrongValues.forEach {
            mockMvc
                .perform(get("/API/tickets/${it}/statusChanges").contentType("application/json"))
                .andExpect(status().isUnprocessableEntity)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// POST /API/tickets
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun createTicket() {
        val newTicketDTO = TicketTestUtils.getNewTicketDTO()

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(
                post("/API/tickets")
                    .content(jsonBody)
                    .contentType("application/json")
            )
            .andExpect(status().isCreated)
            .andExpect { jsonPath("$.ticketId").exists() }
    }

    @Test
    @Transactional
    @Rollback
    fun createTicketCustomerNotFound() {
        val newTicketDTO = TicketTestUtils.getNewTicketDTO().copy(
            customerId = Int.MAX_VALUE
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(
                post("/API/tickets")
                    .content(jsonBody)
                    .contentType("application/json")
            )
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    @Rollback
    fun createTicketProductNotFound() {
        val newTicketDTO = TicketTestUtils.getNewTicketDTO().copy(
            productId = Int.MAX_VALUE
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(
                post("/API/tickets")
                    .content(jsonBody)
                    .contentType("application/json")
            )
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    @Rollback
    fun createTicketDuplicateCustomerProduct() {
        val newTicketDTO = testTickets[0].toDTO().copy()

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(
                post("/API/tickets")
                    .content(jsonBody)
                    .contentType("application/json")
            )
            .andExpect(status().isConflict)
    }

    @Test
    @Transactional
    @Rollback
    fun createTicketBlankFields() {
        val newTicketDTO = TicketTestUtils.getNewTicketDTO().copy(
            title = "",
            description = ""
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(
                post("/API/tickets")
                    .content(jsonBody)
                    .contentType("application/json")
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.error").exists() }
    }

    @Test
    fun createTicketFieldsWithSpaces() {
        val newTicketDTO = TicketTestUtils.getNewTicketDTO().copy(
            title = "   ",
            description = " "
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(
                post("/API/tickets")
                    .content(jsonBody)
                    .contentType("application/json")
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.error").exists() }
    }

    @Test
    fun createTicketInvalidCustomerIdOrProductId() {
        val newTicketDTO = TicketTestUtils.getNewTicketDTO().copy(
            customerId = -1,
            productId = -1
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(
                post("/API/tickets")
                    .content(jsonBody)
                    .contentType("application/json")
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.error").exists() }
    }

    /////////////////////////////////////////////////////////////////////
    ////// PUT /API/tickets/{ticketId}/start
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun startTicketById() {
        val oldTicketDTO = testTickets[0].toDTO()
        val newTicketDTO = oldTicketDTO.copy(
            expertId = TicketTestUtils.experts[0].id,
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
    fun startTicketByIdWrongType() {
        val oldTicketDTO = testTickets[0].toDTO()
        val newTicketDTO = oldTicketDTO.copy(
            expertId = 1,
            priorityLevel = "LOW",
            description = ""
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        val wrongIdValues = listOf("wrong", 0, -1, Int.MIN_VALUE)

        wrongIdValues.forEach {
            mockMvc
                .perform(
                    put("/API/tickets/${it}/start")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnprocessableEntity)
        }

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
    @Transactional
    @Rollback
    fun startTicketByIdInProgress() {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

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

    /////////////////////////////////////////////////////////////////////
    ////// PUT /API/tickets/{ticketId}/stop
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun stopTicketById() {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)


        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.EXPERT,
            description = null
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${ticket.toDTO().ticketId}/stop")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun stopTicketByIdNotFound() {
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.EXPERT,
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

    @Test
    @Transactional
    @Rollback
    fun stopTicketByIdNotInProgress() {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = TicketStatus.CLOSED
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)


        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.EXPERT,
            description = null
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${ticket.toDTO().ticketId}/stop")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
    }

    /////////////////////////////////////////////////////////////////////
    ////// PUT /API/tickets/{ticketId}/resolve
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun resolveTicketById() {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = TicketStatus.IN_PROGRESS
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)


        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.EXPERT,
            description = null
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${ticket.toDTO().ticketId}/resolve")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun resolveTicketByIdNotFound() {
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.EXPERT,
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

    @Test
    @Transactional
    @Rollback
    fun resolveTicketByIdAlreadyResolved() {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = TicketStatus.RESOLVED
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)


        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.EXPERT,
            description = null
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${ticket.toDTO().ticketId}/resolve")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    @Transactional
    @Rollback
    fun resolveTicketByIdClosed() {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = TicketStatus.CLOSED
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)


        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.EXPERT,
            description = null
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${ticket.toDTO().ticketId}/resolve")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
    }

    /////////////////////////////////////////////////////////////////////
    ////// PUT /API/tickets/{ticketId}/reopen
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun reopenClosedTicketById() {

        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = TicketStatus.CLOSED
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)


        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.CUSTOMER,
            description = "description"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${ticket.toDTO().ticketId}/reopen")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent)
    }

    @Test
    @Transactional
    @Rollback
    fun reopenResolvedTicketById() {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = TicketStatus.RESOLVED
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)


        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.CUSTOMER,
            description = "description"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${ticket.toDTO().ticketId}/reopen")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun reopenTicketByIdNotFound() {
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.CUSTOMER,
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

    @Test
    @Transactional
    @Rollback
    fun reopenTicketByIdNotClosedOrResolved() {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = TicketStatus.OPEN
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)


        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.CUSTOMER,
            description = "description"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${ticket.toDTO().ticketId}/reopen")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    @Transactional
    @Rollback
    fun reopenTicketByIdWithoutDescription() {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = TicketStatus.CLOSED
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)


        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.CUSTOMER,
            description = null
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${ticket.toDTO().ticketId}/reopen")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
    }

    /////////////////////////////////////////////////////////////////////
    ////// PUT /API/tickets/{ticketId}/close
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun closeTicketById() {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = TicketStatus.IN_PROGRESS
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)


        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.CUSTOMER,
            description = null
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${ticket.toDTO().ticketId}/close")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun closeTicketByIdNotFound() {
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.CUSTOMER,
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

    @Test
    @Transactional
    @Rollback
    fun closeTicketByIdAlreadyClosed() {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = TicketStatus.CLOSED
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)


        val ticketStatusChangeDTO = TicketStatusChangeDTO(
            changedBy = UserType.CUSTOMER,
            description = null
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(ticketStatusChangeDTO)

        mockMvc
            .perform(
                put("/API/tickets/${ticket.toDTO().ticketId}/close")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
    }
}