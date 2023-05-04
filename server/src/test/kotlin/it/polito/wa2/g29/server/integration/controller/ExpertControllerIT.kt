package it.polito.wa2.g29.server.integration.controller

import it.polito.wa2.g29.server.dto.SkillDTO
import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.*
import it.polito.wa2.g29.server.service.TicketStatusChangeService
import it.polito.wa2.g29.server.utils.ExpertTestUtils
import it.polito.wa2.g29.server.utils.ProductTestUtils
import it.polito.wa2.g29.server.utils.ProfileTestUtils
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpertControllerIT : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var ticketStatusChangeService: TicketStatusChangeService

    @Autowired
    private lateinit var expertRepository: ExpertRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var profileRepository: ProfileRepository

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    lateinit var testProducts: List<Product>
    lateinit var testProfiles: List<Profile>
    lateinit var testExperts: List<Expert>

    @BeforeAll
    fun setup() {
        testProducts = ProductTestUtils.insertProducts(productRepository)
        testProfiles = ProfileTestUtils.insertProfiles(profileRepository)
        testExperts = ExpertTestUtils.insertExperts(expertRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/experts/
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun getAllExpertsEmpty() {
        expertRepository.deleteAll()
        mockMvc
            .perform(get("/API/experts").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isEmpty
            )
    }

    @Test
    fun getAllExperts() {
        mockMvc
            .perform(get("/API/experts").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isNotEmpty,
                jsonPath("$[*].expertId").exists(),
                jsonPath("$[*].name").exists(),
                jsonPath("$[*].surname").exists(),
                jsonPath("$[*].email").exists(),
                jsonPath("$[*].country").exists(),
                jsonPath("$[*].city").exists(),
                jsonPath("$[*].skills").isArray,
                jsonPath("$[*].skills[*].expertise").exists(),
                jsonPath("$[*].skills[*].level").exists()
            )
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/experts/{expertId}
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getExpertById() {
        val expert = testExperts[0]
        mockMvc
            .perform(get("/API/experts/${expert.id}").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.expertId").value(expert.id),
                jsonPath("$.name").value(expert.name),
                jsonPath("$.surname").value(expert.surname),
                jsonPath("$.email").value(expert.email),
                jsonPath("$.country").value(expert.country),
                jsonPath("$.city").value(expert.city),
                jsonPath("$.skills").isArray,
                jsonPath<List<SkillDTO>>("$.skills", hasSize(expert.skills.size))
            )
    }

    @Test
    fun getExpertByIdNotFound() {
        val expertId = Int.MAX_VALUE

        mockMvc
            .perform(get("/API/experts/$expertId").contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun getExpertByIdWrongExpertIdValue() {
        val expertIdWrongValues = listOf(0, -1, -2, Int.MIN_VALUE)

        expertIdWrongValues.forEach {
            mockMvc
                .perform(get("/API/experts/${it}").contentType("application/json"))
                .andExpect(status().isUnprocessableEntity)
        }
    }

    @Test
    fun getExpertByIdWrongExpertIdType() {
        mockMvc
            .perform(get("/API/experts/wrongTypeId").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/experts/{expertId}/statusChanges
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun getStatusChangesByExpertIdWithSomeChanges() {
        val expert = testExperts[0]
        val ticketOne = Ticket("title1", "description1", testProducts[0], testProfiles[0]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }
        val ticketTwo = Ticket("title2", "description2", testProducts[0], testProfiles[1]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }
        ExpertTestUtils.addTicket(ticketRepository, expert, ticketOne)
        ExpertTestUtils.addTicket(ticketRepository, expert, ticketTwo)
        ExpertTestUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expert,
            ticketOne,
            TicketStatus.RESOLVED,
            UserType.EXPERT,
            ""
        )
        ExpertTestUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expert,
            ticketOne,
            TicketStatus.CLOSED,
            UserType.CUSTOMER,
            "It works now"
        )
        ExpertTestUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expert,
            ticketTwo,
            TicketStatus.RESOLVED,
            UserType.EXPERT,
            "The issue has been resolved"
        )
        ExpertTestUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expert,
            ticketTwo,
            TicketStatus.CLOSED,
            UserType.EXPERT,
            null
        )

        mockMvc
            .perform(get("/API/experts/${expert.id}/statusChanges").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isNotEmpty,
                jsonPath<List<TicketChangeDTO>>("$", hasSize(3)),
                jsonPath("$[*].ticketId").value(containsInAnyOrder(ticketOne.id, ticketTwo.id, ticketTwo.id)),
                jsonPath("$[*].currentExpertId").value(hasItem(expert.id)),
                jsonPath("$[*].oldStatus").exists(),
                jsonPath("$[*].newStatus").exists(),
                jsonPath("$[*].changedBy").value(hasItem(UserType.EXPERT.toString())),
                jsonPath("$[*].description").exists(),
                jsonPath("$[*].time").exists()
            )
    }

    @Test
    @Transactional
    @Rollback
    fun getStatusChangesByExpertIdWithNoChanges() {
        val expert = testExperts[0]
        val ticketOne = Ticket("title1", "description1", testProducts[0], testProfiles[0]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }
        val ticketTwo = Ticket("title2", "description2", testProducts[0], testProfiles[1]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }
        ExpertTestUtils.addTicket(ticketRepository, expert, ticketOne)
        ExpertTestUtils.addTicket(ticketRepository, expert, ticketTwo)

        mockMvc
            .perform(get("/API/experts/${expert.id}/statusChanges").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isEmpty
            )
    }

    @Test
    fun getStatusChangesByExpertIdNotFound() {
        val expertId = Int.MAX_VALUE

        mockMvc
            .perform(get("/API/experts/${expertId}/statusChanges").contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun getStatusChangesByExpertIdWrongExpertIdType() {
        val expertId = "id-one"

        mockMvc
            .perform(get("/API/experts/${expertId}/statusChanges").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun getStatusChangesByExpertIdWrongExpertIdValue() {
        val expertIdWrongValues = listOf(0, -1, -2, Int.MIN_VALUE)

        expertIdWrongValues.forEach {
            mockMvc
                .perform(get("/API/experts/${it}/statusChanges").contentType("application/json"))
                .andExpect(status().isUnprocessableEntity)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/experts/{expertId}/tickets
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun getTicketsByExpertIdWithManyTickets() {
        val expertOne = testExperts[0]
        val expertTwo = testExperts[1]
        val ticket1ForExpertOne = Ticket("title1", "description1", testProducts[0], testProfiles[0]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }
        val ticket2ForExpertOne = Ticket("title21", "description21", testProducts[1], testProfiles[1]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.CRITICAL
        }
        val ticketForExpertTwo = Ticket("title2", "description2", testProducts[1], testProfiles[0]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }
        ExpertTestUtils.addTicket(ticketRepository, expertOne, ticket1ForExpertOne)
        ExpertTestUtils.addTicket(ticketRepository, expertOne, ticket2ForExpertOne)
        ExpertTestUtils.addTicket(ticketRepository, expertTwo, ticketForExpertTwo)

        mockMvc
            .perform(get("/API/experts/${expertOne.id}/tickets").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isNotEmpty,
                jsonPath<List<TicketChangeDTO>>("$", hasSize(2)),
                jsonPath("$[*].ticketId").value(contains(ticket2ForExpertOne.id, ticket1ForExpertOne.id)),
                jsonPath("$[*].title").value(contains(ticket2ForExpertOne.title, ticket1ForExpertOne.title)),
                jsonPath("$[*].description").value(
                    contains(
                        ticket2ForExpertOne.description,
                        ticket1ForExpertOne.description
                    )
                ),
                jsonPath("$[*].productId").value(
                    contains(
                        ticket2ForExpertOne.product.id,
                        ticket1ForExpertOne.product.id
                    )
                ),
                jsonPath("$[*].customerId").value(
                    contains(
                        ticket2ForExpertOne.customer.id,
                        ticket1ForExpertOne.customer.id
                    )
                ),
                jsonPath("$[*].expertId").value(hasItem(expertOne.id)),
                jsonPath("$[*].totalExchangedMessages").value(hasItem(0)),
                jsonPath("$[*].priorityLevel").value(
                    contains(
                        ticket2ForExpertOne.priorityLevel.toString(),
                        ticket1ForExpertOne.priorityLevel.toString()
                    )
                ),
                jsonPath("$[*].createdAt").value(
                    contains(
                        ticket2ForExpertOne.createdAt,
                        ticket1ForExpertOne.createdAt
                    )
                ),
                jsonPath("$[*].lastModifiedAt").value(
                    contains(
                        ticket2ForExpertOne.lastModifiedAt,
                        ticket1ForExpertOne.lastModifiedAt
                    )
                ),
            )
    }

    @Test
    fun getTicketsByExpertIdWithNoTickets() {
        val expert = testExperts[0]

        mockMvc
            .perform(get("/API/experts/${expert.id}/tickets").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isEmpty
            )
    }

    @Test
    fun getTicketsByExpertIdNotFound() {
        val expertId = Int.MAX_VALUE

        mockMvc
            .perform(get("/API/experts/${expertId}/tickets").contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun getTicketsByExpertIdWrongExpertIdType() {
        val expertId = "id-one"

        mockMvc
            .perform(get("/API/experts/${expertId}/tickets").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun getTicketsByExpertIdWrongExpertIdValue() {
        val expertIdWrongValues = listOf(0, -1, -2, Int.MIN_VALUE)

        expertIdWrongValues.forEach {
            mockMvc
                .perform(get("/API/experts/${it}/tickets").contentType("application/json"))
                .andExpect(status().isUnprocessableEntity)
        }
    }

}
