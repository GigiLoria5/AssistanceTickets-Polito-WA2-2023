package it.polito.wa2.g29.server.integration.controller

import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.utils.TestProductUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
            .andExpectAll {
                status { isOk() }
                content().contentType(MediaType.APPLICATION_JSON)
                jsonPath("$").isArray
                jsonPath("$").isEmpty
            }
    }

    @Test
    fun getAllProducts() {
        mockMvc
            .get("/API/products")
            .andExpectAll {
                status { isOk() }
                content().contentType(MediaType.APPLICATION_JSON)
                jsonPath("$").isArray
                jsonPath("$").isNotEmpty
                jsonPath("$[*].productId").exists()
                jsonPath("$[*].asin").exists()
                jsonPath("$[*].brand").exists()
                jsonPath("$[*].category").exists()
                jsonPath("$[*].manufacturerNumber").exists()
                jsonPath("$[*].name").exists()
                jsonPath("$[*].price").exists()
                jsonPath("$[*].weight").exists()
            }
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/products/{productId}
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getProductById() {
        val product = productRepository.findAll()[0]
        mockMvc
            .get("/API/products/${product.id}")
            .andExpectAll {
                status { isOk() }
                content().contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.productId").value(product.id)
                jsonPath("$.asin").value(product.asin)
                jsonPath("$.brand").value(product.brand)
                jsonPath("$.category").value(product.category)
                jsonPath("$.manufacturerNumber").value(product.manufacturerNumber)
                jsonPath("$.name").value(product.name)
                jsonPath("$.price").value(product.price)
                jsonPath("$.weight").value(product.weight)
            }
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