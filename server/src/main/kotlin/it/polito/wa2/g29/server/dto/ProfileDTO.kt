package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Profile

data class ProfileDTO(
    var profileId: Int?,
    val email: String,
    val name: String,
    val surname: String,
    val phoneNumber: String,
    val address: String,
    val city: String,
    val country: String,
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
    return ProfileDTO(
        id,
        email,
        name,
        surname,
        phoneNumber,
        address,
        city,
        country
    )
}
