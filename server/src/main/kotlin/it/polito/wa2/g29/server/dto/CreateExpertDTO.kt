package it.polito.wa2.g29.server.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern


data class CreateExpertDTO(
    @field:NotBlank @field:Email @field:Pattern(regexp = ProfileDTO.EMAIL_PATTERN) val email: String,
    @field:NotBlank val password: String,
    @field:NotBlank @field:Pattern(regexp = ProfileDTO.NAME_PATTERN) val name: String,
    @field:NotBlank @field:Pattern(regexp = ProfileDTO.NAME_PATTERN) val surname: String,
    @field:NotBlank @field:Pattern(regexp = ProfileDTO.COUNTRY_PATTERN) val country: String,
    @field:NotBlank @field:Pattern(regexp = ProfileDTO.CITY_PATTERN) val city: String,
    @field:NotEmpty val skills: List<SkillDTO>
)