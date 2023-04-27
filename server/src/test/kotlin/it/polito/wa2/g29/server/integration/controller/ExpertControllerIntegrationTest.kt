package it.polito.wa2.g29.server.integration.controller

import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.utils.TestExpertUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

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
            .get("/API/experts")
            .andExpectAll {
                status { isOk() }
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
                MockMvcResultMatchers.jsonPath("$").isArray
                MockMvcResultMatchers.jsonPath("$").isEmpty
            }
    }

    @Test
    fun getAllExperts() {
        mockMvc
            .get("/API/experts")
            .andExpectAll {
                status { isOk() }
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
                MockMvcResultMatchers.jsonPath("$").isArray
                MockMvcResultMatchers.jsonPath("$").isNotEmpty
                MockMvcResultMatchers.jsonPath("$[*].expertId").exists()
                MockMvcResultMatchers.jsonPath("$[*].name").exists()
                MockMvcResultMatchers.jsonPath("$[*].surname").exists()
                MockMvcResultMatchers.jsonPath("$[*].email").exists()
                MockMvcResultMatchers.jsonPath("$[*].skills").isArray
                MockMvcResultMatchers.jsonPath("$[*].skills[*].expertise").exists()
                MockMvcResultMatchers.jsonPath("$[*].skills[*].level").exists()
            }
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/experts/{expertId}
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getExpertById() {
        val expert = expertRepository.findAll()[0]
        mockMvc
            .get("/API/experts/${expert.id}")
            .andExpectAll {
                status { isOk() }
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
                MockMvcResultMatchers.jsonPath("$.expertId").value(expert.id)
                MockMvcResultMatchers.jsonPath("$.name").value(expert.name)
                MockMvcResultMatchers.jsonPath("$.surname").value(expert.surname)
                MockMvcResultMatchers.jsonPath("$.email").value(expert.email)
                MockMvcResultMatchers.jsonPath("$.skills").value(expert.skills)
            }
    }

    @Test
    fun getExpertByIdNotFound() {
        mockMvc
            .get("/API/experts/99999999")
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun getExpertByIdNegative() {
        mockMvc
            .get("/API/experts/-1")
            .andExpect { status { isUnprocessableEntity() } }
    }

    @Test
    fun getExpertByIdZero() {
        mockMvc
            .get("/API/experts/0")
            .andExpect { status { isUnprocessableEntity() } }
    }

    @Test
    fun getExpertByIdWrongType() {
        mockMvc
            .get("/API/experts/pippo")
            .andExpect { status { isUnprocessableEntity() } }
    }

}
