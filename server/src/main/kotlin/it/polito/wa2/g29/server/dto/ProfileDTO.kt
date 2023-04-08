package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Profile
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Null
import jakarta.validation.constraints.Pattern

data class ProfileDTO(
    @field:Null var profileId: Int?,
    @field:NotBlank @field:Email @field:Pattern(regexp = EMAIL_PATTERN) val email: String,
    @field:NotBlank @field:Pattern(regexp = NAME_PATTERN) val name: String,
    @field:NotBlank @field:Pattern(regexp = NAME_PATTERN) val surname: String,
    @field:NotBlank @field:Pattern(regexp = PHONE_NUMBER_PATTERN) val phoneNumber: String,
    @field:NotBlank @field:Pattern(regexp = ADDRESS_PATTERN) val address: String,
    @field:NotBlank @field:Pattern(regexp = CITY_PATTERN) val city: String,
    @field:NotBlank @field:Pattern(regexp = COUNTRY_PATTERN) val country: String,
) {
    companion object {
        const val EMAIL_PATTERN = ".+@([^.]+\\.)+[a-z]*\$"
        const val NAME_PATTERN = "([A-Za-z][a-z]*)+([ '\\-][A-Za-z]+)*[/.']?"
        const val PHONE_NUMBER_PATTERN = "([0-9]{10})"
        const val ADDRESS_PATTERN = "^[0-9A-Za-z]+([^0-9A-Za-z]{0,2}[a-zA-Z0-9]+)*\$"
        const val CITY_PATTERN = "[a-zA-Z]+([ \\-][a-zA-Z]+)*\$"
        const val COUNTRY_PATTERN = "[a-zA-Z]+( [a-zA-Z]+)*\$"
    }
}

fun Profile.toDTO(): ProfileDTO {
    return ProfileDTO(profileId, email, name, surname, phoneNumber, address, city, country)
}