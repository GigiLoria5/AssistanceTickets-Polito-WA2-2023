package it.polito.wa2.g29.server.integration.controller

import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.utils.ExpertTestUtils
import it.polito.wa2.g29.server.utils.ProductTestUtils
import it.polito.wa2.g29.server.utils.ProfileTestUtils
import it.polito.wa2.g29.server.utils.SecurityTestUtils
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
class ProductControllerIT : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var profileRepository: ProfileRepository

    @Autowired
    private lateinit var expertRepository: ExpertRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

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
    ////// GET /API/products/
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun getAllProductsEmpty() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        productRepository.deleteAllInBatch()
        mockMvc
            .perform(get("/API/products").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isEmpty
            )
    }

    @Test
    fun getAllProductsClient() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        mockMvc
            .perform(get("/API/products").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isNotEmpty,
                jsonPath("$[*].productId").exists(),
                jsonPath("$[*].asin").exists(),
                jsonPath("$[*].brand").exists(),
                jsonPath("$[*].category").exists(),
                jsonPath("$[*].manufacturerNumber").exists(),
                jsonPath("$[*].name").exists(),
                jsonPath("$[*].price").exists(),
                jsonPath("$[*].weight").exists()
            )
    }

    @Test
    fun getAllProductsExpert() {
        SecurityTestUtils.setExpert(testExperts[0].email)
        mockMvc
            .perform(get("/API/products").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isNotEmpty,
                jsonPath("$[*].productId").exists(),
                jsonPath("$[*].asin").exists(),
                jsonPath("$[*].brand").exists(),
                jsonPath("$[*].category").exists(),
                jsonPath("$[*].manufacturerNumber").exists(),
                jsonPath("$[*].name").exists(),
                jsonPath("$[*].price").exists(),
                jsonPath("$[*].weight").exists()
            )
    }

    @Test
    fun getAllProductsManager() {
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/products").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").isArray,
                jsonPath("$").isNotEmpty,
                jsonPath("$[*].productId").exists(),
                jsonPath("$[*].asin").exists(),
                jsonPath("$[*].brand").exists(),
                jsonPath("$[*].category").exists(),
                jsonPath("$[*].manufacturerNumber").exists(),
                jsonPath("$[*].name").exists(),
                jsonPath("$[*].price").exists(),
                jsonPath("$[*].weight").exists()
            )
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/products/{productId}
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getProductById() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val product = testProducts[0]
        mockMvc
            .perform(get("/API/products/${product.id}").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.productId").value(product.id),
                jsonPath("$.asin").value(product.asin),
                jsonPath("$.brand").value(product.brand),
                jsonPath("$.category").value(product.category),
                jsonPath("$.manufacturerNumber").value(product.manufacturerNumber),
                jsonPath("$.name").value(product.name),
                jsonPath("$.price").value(product.price),
                jsonPath("$.weight").value(product.weight)
            )
    }

    @Test
    fun getProductByIdNotFound() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        mockMvc
            .perform(get("/API/products/99999999").contentType("application/json"))
            .andExpect(status().isNotFound)
    }

    @ParameterizedTest
    @ValueSource(
        ints = [0, -1, -2, Int.MIN_VALUE]
    )
    fun getProductByInvalidId(id: Int) {
        SecurityTestUtils.setClient(testProfiles[0].email)
        mockMvc
            .perform(get("/API/products/${id}").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun getProductByIdWrongType() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        mockMvc
            .perform(get("/API/products/wrongTypeId").contentType("application/json"))
            .andExpect(status().isUnprocessableEntity)
    }
}