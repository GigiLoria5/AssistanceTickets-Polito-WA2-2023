package it.polito.wa2.g29.server.integration.controller

import it.polito.wa2.g29.server.dto.SkillDTO
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.*
import it.polito.wa2.g29.server.utils.ExpertTestUtils
import it.polito.wa2.g29.server.utils.ProductTestUtils
import it.polito.wa2.g29.server.utils.ProfileTestUtils
import it.polito.wa2.g29.server.utils.SecurityTestUtils
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
    private lateinit var expertRepository: ExpertRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var profileRepository: ProfileRepository

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
        SecurityTestUtils.setManager()
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
    fun getAllExpertsManager() {
        SecurityTestUtils.setManager()
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

    @Test
    fun getAllExpertsClient() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        mockMvc
            .perform(get("/API/experts").contentType("application/json"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun getAllExpertsExpert() {
        SecurityTestUtils.setExpert(testExperts[0].email)
        mockMvc
            .perform(get("/API/experts").contentType("application/json"))
            .andExpect(status().isUnauthorized)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/experts/{expertId}
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getExpertByIdExpert() {
        SecurityTestUtils.setExpert(testExperts[0].email)
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
    fun getExpertByIdManager() {
        SecurityTestUtils.setManager()
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
    fun getExpertByIdClient() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val expert = testExperts[0]
        mockMvc
            .perform(get("/API/experts/${expert.id}").contentType("application/json"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun getExpertByIdNotFound() {
        SecurityTestUtils.setManager()
        val expertId = Int.MAX_VALUE

        mockMvc
            .perform(get("/API/experts/$expertId").contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, -2, Int.MIN_VALUE]
    )
    fun getExpertByIdWrongExpertIdValue(id: Int) {
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/experts/${id}").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun getExpertByIdWrongExpertIdType() {
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/experts/wrongTypeId").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/experts/{expertId}/statusChanges
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getStatusChangesByExpertIdNotFound() {
        SecurityTestUtils.setManager()
        val expertId = Int.MAX_VALUE

        mockMvc
            .perform(get("/API/experts/${expertId}/statusChanges").contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun getStatusChangesByExpertIdWrongExpertIdType() {
        SecurityTestUtils.setManager()
        val expertId = "id-one"

        mockMvc
            .perform(get("/API/experts/${expertId}/statusChanges").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, -2, Int.MIN_VALUE]
    )
    fun getStatusChangesByExpertIdWrongExpertIdValue(id: Int) {
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/experts/${id}/statusChanges").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/experts/{expertId}/tickets
    /////////////////////////////////////////////////////////////////////


    @Test
    fun getTicketsByExpertIdWithNoTickets() {
        SecurityTestUtils.setManager()
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
        SecurityTestUtils.setManager()
        val expertId = Int.MAX_VALUE

        mockMvc
            .perform(get("/API/experts/${expertId}/tickets").contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun getTicketsByExpertIdWrongExpertIdType() {
        SecurityTestUtils.setManager()
        val expertId = "id-one"

        mockMvc
            .perform(get("/API/experts/${expertId}/tickets").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, -2, Int.MIN_VALUE]
    )
    fun getTicketsByExpertIdWrongExpertIdValue(id: Int) {
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/experts/${id}/tickets").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

}
