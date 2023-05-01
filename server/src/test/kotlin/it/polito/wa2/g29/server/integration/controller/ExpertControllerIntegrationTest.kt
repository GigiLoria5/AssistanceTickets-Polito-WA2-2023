package it.polito.wa2.g29.server.integration.controller

import it.polito.wa2.g29.server.dto.SkillDTO
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.utils.TestExpertUtils
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ExpertControllerIntegrationTest : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var expertRepository: ExpertRepository

    @BeforeEach
    fun setup() {
        expertRepository.deleteAll()
        TestExpertUtils.insertExperts(expertRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/experts/
    /////////////////////////////////////////////////////////////////////

    @Test
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
        val expert = expertRepository.findAll()[0]
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
        mockMvc
            .perform(get("/API/experts/99999999").contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun getExpertByIdNegative() {
        mockMvc
            .perform(get("/API/experts/-1").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun getExpertByIdZero() {
        mockMvc
            .perform(get("/API/experts/0").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun getExpertByIdWrongType() {
        mockMvc
            .perform(get("/API/experts/wrongTypeId").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

}
