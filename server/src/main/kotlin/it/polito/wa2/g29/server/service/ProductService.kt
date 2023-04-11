package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.ProductDTO

interface ProductService {
    fun getAllProducts(): List<ProductDTO>

    fun getProductById(productId: Int): ProductDTO
}