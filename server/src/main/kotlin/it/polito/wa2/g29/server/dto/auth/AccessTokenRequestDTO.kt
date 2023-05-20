package it.polito.wa2.g29.server.dto.auth

import it.polito.wa2.g29.server.dto.ProfileDTO
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class AccessTokenRequestDTO(
    @field:NotBlank @field:Email @field:Pattern(regexp = ProfileDTO.EMAIL_PATTERN) val username: String,
    @field:NotBlank val password: String
)
