package it.polito.wa2.g29.server.controller

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g29.server.dto.ProductDTO
import it.polito.wa2.g29.server.dto.ProductTokenDTO
import it.polito.wa2.g29.server.service.ProductService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Observed
@RequestMapping("/API")
@Validated
@RestController
class ProductController(private val productService: ProductService) {

    private val log = LoggerFactory.getLogger(ProductController::class.java)

    @GetMapping("/products")
    fun getAllProducts(): List<ProductDTO> {
        log.info("Retrieve all products")
        return productService.getAllProducts()
    }

    @GetMapping("/products/{productId}")
    fun getProductById(@PathVariable @Valid @Min(1) productId: Int): ProductDTO {
        log.info("Retrieve product: {}", productId)
        return productService.getProductById(productId)
    }

    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER)")
    @PostMapping("/products/{productId}/token")
    fun generateProductToken(@PathVariable @Valid @Min(1) productId: Int): ProductTokenDTO {
        return productService.generateProductToken(productId)
    }

}
