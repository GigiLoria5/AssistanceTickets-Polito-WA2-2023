package it.polito.wa2.g29.server.dto

import jakarta.validation.constraints.Size

data class RegisterTokenDTO(
    @field:Size(min = 36, max = 36)
    val token: String
)
