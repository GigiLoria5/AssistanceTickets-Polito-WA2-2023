package it.polito.wa2.g29.server.dto.profile

import jakarta.annotation.Nullable
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class EditProfileDTO(
    @field:NotBlank @field:Pattern(regexp = NAME_PATTERN) val name: String,
    @field:NotBlank @field:Pattern(regexp = NAME_PATTERN) val surname: String,
    @field:NotBlank @field:Pattern(regexp = PHONE_NUMBER_PATTERN) val phoneNumber: String,
    @field:NotBlank @field:Pattern(regexp = ADDRESS_PATTERN) val address: String,
    @field:NotBlank @field:Pattern(regexp = CITY_PATTERN) val city: String,
    @field:NotBlank @field:Pattern(regexp = COUNTRY_PATTERN) val country: String,
    @field:Nullable val ticketsIds: List<Int?>?
) {
    companion object {
        const val NAME_PATTERN = "([A-Za-z][a-z]*)+([ '\\-][A-Za-z]+)*[/.']?"
        const val PHONE_NUMBER_PATTERN = "([0-9]{10})"
        const val ADDRESS_PATTERN = "^[0-9A-Za-z]+([^0-9A-Za-z]{0,2}[a-zA-Z0-9]+)*\$"
        const val CITY_PATTERN = "[a-zA-Z]+([ \\-][a-zA-Z]+)*\$"
        const val COUNTRY_PATTERN = "[a-zA-Z]+( [a-zA-Z]+)*\$"
    }
}
