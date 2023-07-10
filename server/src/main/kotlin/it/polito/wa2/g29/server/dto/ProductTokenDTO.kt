package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.ProductToken

data class ProductTokenDTO(
    val productTokenId: Int?,
    val createdAt: Long,
    val registeredAt: Long,
    val token: String,
    val userId: Int?,
    val product: ProductDTO?
)

fun ProductToken.toDTO(): ProductTokenDTO {
    return ProductTokenDTO(id, createdAt, registeredAt, token, user?.id, product.toDTO())
}
