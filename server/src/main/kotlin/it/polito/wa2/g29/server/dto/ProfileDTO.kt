package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Profile
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Null

data class ProfileDTO(
    @field:Null val profileId: Int?,
    @field:Email @field:NotBlank val email: String?,
    @field:NotBlank var name: String?,
    @field:NotBlank var surname: String?,
    @field:NotBlank var phoneNumber: String?,
    @field:NotBlank var address: String?,
    @field:NotBlank var city: String?,
    @field:NotBlank var country: String?
)

fun Profile.toDTO(): ProfileDTO {
    return ProfileDTO(profileId, email, name, surname, phoneNumber, address, city, country)
}