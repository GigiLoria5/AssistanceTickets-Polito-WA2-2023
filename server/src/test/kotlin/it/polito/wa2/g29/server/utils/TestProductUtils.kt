package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.repository.ProductRepository

object TestProductUtils {
    fun insertProducts(productRepository: ProductRepository) {
        val products = listOf(
            Product().apply {
                productId = 1
                asin = "B08H8LGXC1"
                brand = "Apple"
                category = "Electronics"
                manufacturerNumber = "MWP22AM/A"
                name = "Apple AirPods Pro"
                price = 249.0F
                weight = 0.22F
            },
            Product().apply {
                productId = 2
                asin = "B07W6VGBKF"
                brand = "Amazon"
                category = "Electronics"
                manufacturerNumber = "53-021661"
                name = "Echo Dot (3rd Gen)"
                price = 39.99F
                weight = 0.65F
            }
        )
        productRepository.saveAll(products)
    }
}