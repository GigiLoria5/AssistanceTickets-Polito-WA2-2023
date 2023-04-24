package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Product

data class ProductDTO(
    val productId: Int?,
    val asin: String,
    val brand: String,
    val category: String,
    val manufacturerNumber: String,
    val name: String,
    val price: Float,
    val weight: Float,
)

fun Product.toDTO(): ProductDTO {
    return ProductDTO(id, asin, brand, category, manufacturerNumber, name, price, weight)
}
