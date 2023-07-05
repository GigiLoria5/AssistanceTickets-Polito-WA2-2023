package it.polito.wa2.g29.server.dto

data class UserDTO(
    val id: Int? = null,
    val email: String,
    val name: String,
    val role: String,
)
