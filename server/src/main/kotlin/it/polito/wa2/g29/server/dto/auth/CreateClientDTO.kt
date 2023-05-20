package it.polito.wa2.g29.server.dto.auth

import it.polito.wa2.g29.server.dto.ProfileDTO
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateClientDTO(
    @field:NotBlank @field:Email @field:Pattern(regexp = ProfileDTO.EMAIL_PATTERN) val email: String,
    @field:NotBlank val password: String,
    @field:NotBlank @field:Pattern(regexp = ProfileDTO.NAME_PATTERN) val name: String,
    @field:NotBlank @field:Pattern(regexp = ProfileDTO.NAME_PATTERN) val surname: String,
    @field:NotBlank @field:Pattern(regexp = ProfileDTO.PHONE_NUMBER_PATTERN) val phoneNumber: String,
    @field:NotBlank @field:Pattern(regexp = ProfileDTO.ADDRESS_PATTERN) val address: String,
    @field:NotBlank @field:Pattern(regexp = ProfileDTO.CITY_PATTERN) val city: String,
    @field:NotBlank @field:Pattern(regexp = ProfileDTO.COUNTRY_PATTERN) val country: String,
)
