package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Profile

data class ProfileDTO(
    val profileId: Int,
    val email: String,
    val name: String,
    val surname: String,
    val phoneNumber: String,
    val address: String,
    val city: String,
    val country: String
)

fun Profile.toDTO(): ProfileDTO {
    return ProfileDTO(profileId, email, name, surname, phoneNumber, address, city, country)
}