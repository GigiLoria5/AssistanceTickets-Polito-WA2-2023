package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.repository.ProductRepository

object TestProductUtils {

    private val products = listOf(
        Product(
            asin = "B08H8LGXC1",
            brand = "Apple",
            category = "Electronics",
            manufacturerNumber = "MWP22AM/A",
            name = "Apple AirPods Pro",
            price = 249.0F,
            weight = 0.22F
        ),
        Product(
            asin = "B07W6VGBKF",
            brand = "Amazon",
            category = "Electronics",
            manufacturerNumber = "53-021661",
            name = "Echo Dot (3rd Gen)",
            price = 39.99F,
            weight = 0.65F
        )
    )

    private fun getProducts(): List<Product> {
        return products.map {
            Product(
                it.asin,
                it.brand,
                it.category,
                it.manufacturerNumber,
                it.name,
                it.price,
                it.weight
            )
        }
    }

    fun insertProducts(productRepository: ProductRepository): List<Product> {
        val newProducts = getProducts()
        productRepository.saveAll(newProducts)
        return newProducts
    }
}