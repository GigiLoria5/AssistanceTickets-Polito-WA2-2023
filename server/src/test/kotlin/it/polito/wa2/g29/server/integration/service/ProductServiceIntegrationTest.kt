package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.ProductNotFoundException
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.service.ProductService
import it.polito.wa2.g29.server.utils.TestProductUtils
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductServiceIntegrationTest : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var productRepository: ProductRepository

    lateinit var testProducts: List<Product>

    @BeforeAll
    fun setup() {
        testProducts = TestProductUtils.insertProducts(productRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// getAllProducts
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun getAllProductsEmpty() {
        productRepository.deleteAllInBatch()

        val products = productService.getAllProducts()

        assert(products.isEmpty())
    }

    @Test
    fun getAllProducts() {
        val expectedProducts = testProducts

        val actualProducts = productService.getAllProducts()

        assert(actualProducts.isNotEmpty())
        assert(actualProducts.size == expectedProducts.size)
        expectedProducts.forEach {
            actualProducts.contains(it.toDTO())
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// getProductById
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getProductById() {
        val expectedProductDTO = productRepository.findAll()[0].toDTO()

        val actualProductDTO = productService.getProductById(expectedProductDTO.productId!!)

        assert(actualProductDTO == expectedProductDTO)
    }

    @Test
    fun getProductByIdNotFound() {
        assertThrows<ProductNotFoundException> {
            productService.getProductById(Int.MAX_VALUE)
        }
    }
}