package it.polito.wa2.g29.server.controller

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g29.server.dto.ProductDTO
import it.polito.wa2.g29.server.observation.annotation.LogInfo
import it.polito.wa2.g29.server.service.ProductService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Observed
@RequestMapping("/API")
@Validated
@RestController
class ProductController(private val productService: ProductService) {
    private val log = LoggerFactory.getLogger(ProductController::class.java)
    // GET /API/products/ -- list all registered products in the DB
    @GetMapping("/products")
    fun getAllProducts(): List<ProductDTO> {
        log.info("Retrieve all products")
        return productService.getAllProducts()
    }

    // GET /API/products/{productId} -- details of product {productId} or fail if it does not exist
    @GetMapping("/products/{productId}")
    fun getProductById(@PathVariable @Valid @Min(1) productId: Int): ProductDTO {
        log.info("Retrieve product: {}", productId)
        return productService.getProductById(productId)
    }

}
