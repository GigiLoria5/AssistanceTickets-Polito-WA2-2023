package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.ProductDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.ProductNotFoundException
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.service.ProductService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository
) : ProductService {
    override fun getAllProducts(): List<ProductDTO> {
        return productRepository.findAll().map { it.toDTO() }
    }

    override fun getProductById(productId: Int): ProductDTO {
        val product = productRepository.findByIdOrNull(productId) ?: throw ProductNotFoundException()
        return product.toDTO()
    }
}