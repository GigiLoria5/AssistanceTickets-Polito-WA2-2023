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
        productRepository.deleteAll()
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

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/products/{productId}
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getProductById() {
        val product = TestProductUtils.products[0]
        mockMvc
            .get("/API/products/${product.productId}")
            .andExpect { status { isOk() } }
            .andExpect { content().contentType(MediaType.APPLICATION_JSON) }
            .andExpect { jsonPath("$.productId").value(product.productId) }
            .andExpect { jsonPath("$.asin").value(product.asin) }
            .andExpect { jsonPath("$.brand").value(product.brand) }
            .andExpect { jsonPath("$.category").value(product.category) }
            .andExpect { jsonPath("$.manufacturerNumber").value(product.manufacturerNumber) }
            .andExpect { jsonPath("$.name").value(product.name) }
            .andExpect { jsonPath("$.price").value(product.price) }
            .andExpect { jsonPath("$.weight").value(product.weight) }
    }

    @Test
    fun getProductByIdNotFound() {
        mockMvc
            .get("/API/products/99999999")
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun getProductByIdNegative() {
        mockMvc
            .get("/API/products/-1")
            .andExpect { status { isUnprocessableEntity() } }
    }

    @Test
    fun getProductByIdZero() {
        mockMvc
            .get("/API/products/0")
            .andExpect { status { isUnprocessableEntity() } }
    }

    @Test
    fun getProductByIdWrongType() {
        mockMvc
            .get("/API/products/pippo")
            .andExpect { status { isUnprocessableEntity() } }
    }
}