package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Profile
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class ProfileDTO(
    val profileId: Int?,
    @field:Email @field:NotBlank val email: String?,
    @field:NotBlank val name: String?,
    @field:NotBlank val surname: String?,
    @field:NotBlank val phoneNumber: String?,
    @field:NotBlank val address: String?,
    @field:NotBlank val city: String?,
    @field:NotBlank val country: String?
)

fun Profile.toDTO(): ProfileDTO {
    return ProfileDTO(profileId, email, name, surname, phoneNumber, address, city, country)
}