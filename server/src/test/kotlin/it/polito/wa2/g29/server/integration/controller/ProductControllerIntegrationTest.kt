package it.polito.wa2.g29.server.integration.controller

import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.utils.TestProductUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@AutoConfigureMockMvc
class ProductControllerIntegrationTest : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var productRepository: ProductRepository

    @BeforeEach
    fun setup() {
        TestProductUtils.insertProducts(productRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/products/
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getAllProductsEmpty() {
        productRepository.deleteAll()
        mockMvc
            .get("/API/products")
            .andExpect { status { isOk() } }
            .andExpect { content().contentType(MediaType.APPLICATION_JSON) }
            .andExpect { jsonPath("$").isArray }
            .andExpect { jsonPath("$").isEmpty }
    }

    @Test
    fun getAllProducts() {
        mockMvc
            .get("/API/products")
            .andExpect { status { isOk() } }
            .andExpect { content().contentType(MediaType.APPLICATION_JSON) }
            .andExpect { jsonPath("$").isArray }
            .andExpect { jsonPath("$").isNotEmpty }
            .andExpect { jsonPath("$[*].productId").exists() }
            .andExpect { jsonPath("$[*].asin").exists() }
            .andExpect { jsonPath("$[*].brand").exists() }
            .andExpect { jsonPath("$[*].category").exists() }
            .andExpect { jsonPath("$[*].manufacturerNumber").exists() }
            .andExpect { jsonPath("$[*].name").exists() }
            .andExpect { jsonPath("$[*].price").exists() }
            .andExpect { jsonPath("$[*].weight").exists() }
    }
}