package it.polito.wa2.g29.server.dto.auth

import jakarta.validation.constraints.NotBlank

data class AccessTokenRequestDTO(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String

)
