package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.repository.ProductRepository

object TestProductUtils {

    /**
     * Inserts a list of new products into the provided [productRepository] and returns an array of the newly added products.
     * @param productRepository the repository where the products should be saved
     * @return an array of the newly added products, with a guaranteed size of 2
     */
    fun insertProducts(productRepository: ProductRepository): List<Product> {
        val newProducts = getProducts()
        productRepository.saveAll(newProducts)
        return newProducts
    }

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

}
