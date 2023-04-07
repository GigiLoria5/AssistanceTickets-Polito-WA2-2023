package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Profile
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Null
import jakarta.validation.constraints.Pattern

data class ProfileDTO(
    @field:Null var profileId: Int?,
    @field:NotBlank @field:Pattern(regexp = EMAIL_PATTERN) val email: String,
    @field:NotBlank @field:Pattern(regexp = NAME_PATTERN) val name: String,
    @field:NotBlank @field:Pattern(regexp = NAME_PATTERN) val surname: String,
    @field:NotBlank @field:Pattern(regexp = PHONE_NUMBER_PATTERN) val phoneNumber: String,
    @field:NotBlank val address: String,
    @field:NotBlank val city: String,
    @field:NotBlank val country: String
) {
    companion object {
        const val EMAIL_PATTERN = "([a-z0-9._]+@[a-z0-9.-]+\\.[a-z]{2,3})"
        const val NAME_PATTERN = "([A-Z][a-z]*)+([ '-][A-Za-z]+)*\\.?"
        const val PHONE_NUMBER_PATTERN = "([0-9]{10})"
    }
}

fun Profile.toDTO(): ProfileDTO {
    return ProfileDTO(profileId, email, name, surname, phoneNumber, address, city, country)
}