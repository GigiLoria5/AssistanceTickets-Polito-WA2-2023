package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.ProductDTO
import it.polito.wa2.g29.server.dto.ProductTokenDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.ProductNotFoundException
import it.polito.wa2.g29.server.model.ProductToken
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProductTokenRepository
import it.polito.wa2.g29.server.service.ProductService
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service

class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val productTokenRepository: ProductTokenRepository
) : ProductService {

    private val log = LoggerFactory.getLogger(ProductServiceImpl::class.java)

    override fun getAllProducts(): List<ProductDTO> {
        return productRepository.findAll().map { it.toDTO() }
    }

    override fun getProductById(productId: Int): ProductDTO {
        val product = productRepository.findByIdOrNull(productId)
            ?: run {
                log.info("Product not found")
                throw ProductNotFoundException()
            }
        return product.toDTO()
    }

    override fun generateProductToken(productId: Int): ProductTokenDTO {
        val product = productRepository.findByIdOrNull(productId)
            ?: run {
                log.info("Product not found")
                throw ProductNotFoundException()
            }
        val productToken = ProductToken(product)
        productTokenRepository.save(productToken)
        return ProductTokenDTO(productToken.token)
    }

}
