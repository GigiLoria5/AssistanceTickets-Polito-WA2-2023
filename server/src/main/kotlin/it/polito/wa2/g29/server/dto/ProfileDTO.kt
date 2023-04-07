package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Profile
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Null
import jakarta.validation.constraints.Pattern

data class ProfileDTO(
    @field:Null var profileId: Int?,
    @field:NotBlank @field:Pattern(regexp = "([a-z0-9._]+@[a-z0-9.-]+\\.[a-z]{2,3})") val email: String,
    @field:NotBlank @field:Pattern(regexp = "([A-Za-z ]+[.'-]{0,1}[A-Za-z ]*)") val name: String,
    @field:NotBlank @field:Pattern(regexp = "([A-Za-z ]+[.'-]{0,1}[A-Za-z ]*)") val surname: String,
    @field:NotBlank @field:Pattern(regexp = "([0-9]{10})") val phoneNumber: String,
    @field:NotBlank val address: String,
    @field:NotBlank val city: String,
    @field:NotBlank val country: String
)

fun Profile.toDTO(): ProfileDTO {
    return ProfileDTO(profileId, email, name, surname, phoneNumber, address, city, country)
}