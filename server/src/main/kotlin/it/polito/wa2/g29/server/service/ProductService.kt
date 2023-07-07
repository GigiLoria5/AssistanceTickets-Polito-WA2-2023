package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.ProductDTO
import it.polito.wa2.g29.server.dto.TokenDTO

interface ProductService {
    fun getAllProducts(): List<ProductDTO>

    fun getProductById(productId: Int): ProductDTO

    fun generateProductToken(productId: Int): TokenDTO

    fun registerProduct(token: String)
}
