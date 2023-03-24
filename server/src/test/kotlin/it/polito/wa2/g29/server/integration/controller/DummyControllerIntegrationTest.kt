package it.polito.wa2.g29.server.integration.controller

import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc

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