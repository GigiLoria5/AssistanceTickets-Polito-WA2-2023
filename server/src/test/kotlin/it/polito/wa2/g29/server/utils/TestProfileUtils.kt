package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ProfileRepository

object TestProfileUtils {
    val profiles = listOf(
        Profile().apply {
            email = "email1@wa2.it"
            name = "Shadow"
            surname = "Deighton"
            phoneNumber = "646-200-7344"
            address = "4 Pepper Wood Street"
            city = "Humble"
            country = "United States"
        },
        Profile().apply {
            email = "email2@wa2.it"
            name = "John"
            surname = "Kerner"
            phoneNumber = "330-551-6835"
            address = "Corso Duca degli Abruzzi, 24"
            city = "Turin"
            country = "Italy"
        },
    )

    val newProfileDTO = ProfileDTO(
        profileId = null,
        email = "new_mail@test.com",
        name = "NewName",
        surname = "NewSurname",
        phoneNumber = "333-333-3333",
        address = "NewAddress",
        city = "NewCity",
        country = "NewCountry"
    )

    fun insertProfiles(profileRepository: ProfileRepository) {
        profileRepository.saveAll(profiles)
    }
}