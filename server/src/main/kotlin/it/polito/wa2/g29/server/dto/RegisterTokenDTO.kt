package it.polito.wa2.g29.server.dto

import jakarta.validation.constraints.NotBlank

data class RegisterTokenDTO(
    @field:NotBlank
    val token: String)
