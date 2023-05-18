package it.polito.wa2.g29.server.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketStatusChangeService
import it.polito.wa2.g29.server.utils.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
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

    lateinit var testProducts: List<Product>

    lateinit var testExperts: List<Expert>
    @BeforeAll
    fun prepare() {
        testProducts = ProductTestUtils.insertProducts(productRepository)
        testProfiles = ProfileTestUtils.insertProfiles(profileRepository)
        testExperts = ExpertTestUtils.insertExperts(expertRepository)
        TicketTestUtils.products = testProducts
        TicketTestUtils.profiles = testProfiles
        TicketTestUtils.experts = testExperts
        testTickets = TicketTestUtils.insertTickets(ticketRepository)
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
    fun getAllTicketsManager() {
        SecurityTestUtils.setManager()
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
    fun getAllTicketsOpen() {
        SecurityTestUtils.setManager()
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
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/tickets").queryParam("status", "OPENED").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/tickets/{ticketId}
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getTicketByIdManager() {
        SecurityTestUtils.setManager()
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
                jsonPath("$.expertId").value(ticket.expert?.id),
                jsonPath("$.totalExchangedMessages").value(ticket.messages.size),
                jsonPath("$.status").value(ticket.status.toString()),
                jsonPath("$.priorityLevel").value(ticket.priorityLevel?.toString()),
                jsonPath("$.createdAt").value(ticket.createdAt),
                jsonPath("$.lastModifiedAt").value(ticket.lastModifiedAt)
            )
    }

    @Test
    fun getTicketByIdClient() {
        SecurityTestUtils.setClient(testTickets[0].customer.email)
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
    @Transactional
    @Rollback
    fun getTicketStatusChangesByIdManager() {
        val expert = TicketTestUtils.experts[0]
        SecurityTestUtils.setManager()

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
    fun getTicketStatusChangesByIdExpert() {
        val expert = TicketTestUtils.experts[0]
        SecurityTestUtils.setExpert(expert.email)

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
        SecurityTestUtils.setClient(testProfiles[0].email)
        val ticketOne =
            Ticket("title1", "description1", testProducts[0], testProfiles[0]).apply {
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
    ////// POST /API/tickets
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun createTicket() {
        val newTicketDTO = TicketTestUtils.getNewTicketDTO()
        SecurityTestUtils.setClient(testProfiles[1].email)
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
    fun createTicketManager() {
        val newTicketDTO = TicketTestUtils.getNewTicketDTO()
        SecurityTestUtils.setManager()
        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(
                post("/API/tickets")
                    .content(jsonBody)
                    .contentType("application/json")
            )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @Transactional
    @Rollback
    fun createTicketExpert() {
        val newTicketDTO = TicketTestUtils.getNewTicketDTO()
        SecurityTestUtils.setExpert(testExperts[1].email)
        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newTicketDTO)

        mockMvc
            .perform(
                post("/API/tickets")
                    .content(jsonBody)
                    .contentType("application/json")
            )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @Transactional
    @Rollback
    fun createTicketProductNotFound() {
        val newTicketDTO = TicketTestUtils.getNewTicketDTO().copy(
            productId = Int.MAX_VALUE
        )
        SecurityTestUtils.setClient(testProfiles[0].email)

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

        SecurityTestUtils.setClient(testTickets[0].customer.email)
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

        SecurityTestUtils.setClient(testProfiles[0].email)
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
        SecurityTestUtils.setClient(testProfiles[0].email)

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
    fun createTicketInvalidProductId() {
        val newTicketDTO = TicketTestUtils.getNewTicketDTO().copy(
            productId = -1
        )
        SecurityTestUtils.setClient(testProfiles[0].email)

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

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class, names = ["OPEN", "REOPENED"]
    )
    @Transactional
    @Rollback
    fun startTicketById(ticketStatus: TicketStatus) {
        SecurityTestUtils.setManager()
        val oldTicketDTO = testTickets[0].toDTO()
        val newTicketDTO = oldTicketDTO.copy(
            expertId = TicketTestUtils.experts[0].id,
            priorityLevel = "LOW",
            description = "",
            status = ticketStatus.toString()
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
        SecurityTestUtils.setManager()
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
    fun startTicketByIdClient() {
        SecurityTestUtils.setClient(testProfiles[0].email)
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
                put("/API/tickets/${oldTicketDTO.ticketId}/start")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun startTicketByIdExpert() {
        SecurityTestUtils.setExpert(testExperts[0].email)
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
                put("/API/tickets/${oldTicketDTO.ticketId}/start")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized)
    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, -2, Int.MIN_VALUE]
    )
    fun startTicketInvalidId(id: Int) {
        SecurityTestUtils.setManager()
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
                put("/API/tickets/${id}/start")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun startTicketByExpertIdNotFound() {
        SecurityTestUtils.setManager()
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

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class, names = ["IN_PROGRESS", "CLOSED", "RESOLVED"]
    )
    @Transactional
    @Rollback
    fun startTicketByIdWrongStatus(ticketStatus: TicketStatus) {
        SecurityTestUtils.setManager()
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = ticketStatus
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

        SecurityTestUtils.setExpert(expert.email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class, names = ["OPEN", "REOPENED", "CLOSED", "RESOLVED"]
    )
    @Transactional
    @Rollback
    fun stopTicketByIdNotInProgress(ticketStatus: TicketStatus) {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = ticketStatus
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

        SecurityTestUtils.setExpert(expert.email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class, names = ["OPEN", "IN_PROGRESS", "REOPENED"]
    )
    @Transactional
    @Rollback
    fun resolveTicketByIdExpert(ticketStatus: TicketStatus) {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = ticketStatus
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

        SecurityTestUtils.setExpert(expert.email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class, names = ["OPEN", "IN_PROGRESS", "REOPENED"]
    )
    @Transactional
    @Rollback
    fun resolveTicketByIdClientUnauthorized(ticketStatus: TicketStatus) {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = ticketStatus
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

        SecurityTestUtils.setClient(testProfiles[0].email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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
            .andExpect(status().isUnauthorized)
    }

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class, names = ["OPEN", "IN_PROGRESS", "REOPENED"]
    )
    @Transactional
    @Rollback
    fun resolveTicketByIdClient(ticketStatus: TicketStatus) {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = ticketStatus
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

        SecurityTestUtils.setClient(ticket.customer.email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class, names = ["RESOLVED","CLOSED"]
    )
    @Transactional
    @Rollback
    fun resolveTicketByIdWrongStatus(ticketStatus: TicketStatus) {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = ticketStatus
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

        SecurityTestUtils.setExpert(expert.email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class, names = ["RESOLVED", "CLOSED"]
    )
    @Transactional
    @Rollback
    fun reopenTicketById(ticketStatus: TicketStatus) {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = ticketStatus
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

        SecurityTestUtils.setClient(ticket.customer.email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class, names = ["RESOLVED", "CLOSED"]
    )
    @Transactional
    @Rollback
    fun reopenTicketByIdManager(ticketStatus: TicketStatus) {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = ticketStatus
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

        SecurityTestUtils.setManager()
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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
            .andExpect(status().isUnauthorized)
    }

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class, names = ["RESOLVED", "CLOSED"]
    )
    @Transactional
    @Rollback
    fun reopenTicketByIdExpert(ticketStatus: TicketStatus) {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = ticketStatus
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

        SecurityTestUtils.setExpert(expert.email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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
            .andExpect(status().isUnauthorized)
    }

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
    @EnumSource(TicketStatus::class, names = ["OPEN", "REOPENED", "IN_PROGRESS"])
    @Transactional
    @Rollback
    fun reopenTicketByIdWrongStatus(ticketStatus: TicketStatus) {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = ticketStatus
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

        SecurityTestUtils.setClient(ticket.customer.email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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

    @Test
    @Transactional
    @Rollback
    fun reopenTicketByIdWithoutDescription() {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = TicketStatus.CLOSED
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

        SecurityTestUtils.setClient(ticket.customer.email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class, names = ["OPEN", "REOPENED", "IN_PROGRESS", "RESOLVED"]
    )
    @Transactional
    @Rollback
    fun closeTicketByIdManager(ticketStatus: TicketStatus) {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = ticketStatus
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

        SecurityTestUtils.setManager()
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class, names = ["OPEN", "REOPENED", "IN_PROGRESS", "RESOLVED"]
    )
    @Transactional
    @Rollback
    fun closeTicketByIdExpert(ticketStatus: TicketStatus) {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = ticketStatus
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

        SecurityTestUtils.setExpert(expert.email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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

    @ParameterizedTest
    @EnumSource(
        TicketStatus::class, names = ["OPEN", "REOPENED", "IN_PROGRESS", "RESOLVED"]
    )
    @Transactional
    @Rollback
    fun closeTicketByIdClient(ticketStatus: TicketStatus) {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = ticketStatus
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

        SecurityTestUtils.setClient(ticket.customer.email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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
            .andExpect(status().isUnauthorized)
    }

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

    @Test
    @Transactional
    @Rollback
    fun closeTicketByIdAlreadyClosed() {
        val expert = TicketTestUtils.experts[0]

        val ticket = Ticket("title1", "description1", TicketTestUtils.products[0], TicketTestUtils.profiles[1]).apply {
            status = TicketStatus.CLOSED
        }

        TicketTestUtils.addTicket(ticketRepository, expert, ticket)

        SecurityTestUtils.setExpert(expert.email)
        val ticketStatusChangeDTO = TicketStatusChangeDTO(
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