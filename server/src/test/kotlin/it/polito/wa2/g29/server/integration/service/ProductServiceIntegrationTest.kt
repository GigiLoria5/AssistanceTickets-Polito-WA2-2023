package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.service.ProductService
import it.polito.wa2.g29.server.utils.TestProductUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ProductServiceIntegrationTest : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var productRepository: ProductRepository

    @BeforeEach
    fun setup() {
        productRepository.deleteAll()
        TestProductUtils.insertProducts(productRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// getAllProducts
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getAllProductsEmpty() {
        productRepository.deleteAll()

        val products = productService.getAllProducts()

        assert(products.isEmpty())
    }

    @Test
    fun getAllProducts() {
        val expectedProducts = TestProductUtils.products

        val products = productService.getAllProducts()

        assert(products.isNotEmpty())
        assert(products.size == expectedProducts.size)
        expectedProducts.forEach {
            products.contains(it.toDTO())
        }
    }
}