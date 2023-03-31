package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Profile
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Null
import jakarta.validation.constraints.Pattern

data class ProfileDTO(
    @field:Null var profileId: Int?,
    @field:Email @field:NotBlank var email: String,
    @field:NotBlank var name: String,
    @field:NotBlank var surname: String,
    @field:NotBlank @field:Pattern(regexp = "([0-9]{3}-[0-9]{3}-[0-9]{4})") var phoneNumber: String,
    @field:NotBlank var address: String,
    @field:NotBlank var city: String,
    @field:NotBlank var country: String
)

fun Profile.toDTO(): ProfileDTO {
    return ProfileDTO(profileId, email, name, surname, phoneNumber, address, city, country)
}