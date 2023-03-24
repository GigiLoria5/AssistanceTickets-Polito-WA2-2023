package it.polito.wa2.g29.assistancetickets.integration.controller

import it.polito.wa2.g29.assistancetickets.integration.AbstractTestcontainersTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@AutoConfigureMockMvc
class DummyControllerIntegrationTest: AbstractTestcontainersTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    /*
    @Test
    fun dummyTest() {
        mockMvc
            .get("/endpoint")
            .andExpect { status { isOk() } }
    }
*/
}