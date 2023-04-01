package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ProfileRepository

object TestProfileUtils {
    val profiles = listOf(
        Profile().apply {
            profileId = 1
            email = "mail1@wa2.it"
            name = "Shadow"
            surname = "Deighton"
            phoneNumber = "646-200-7344"
            address = "4 Pepper Wood Street"
            city = "Humble"
            country = "United States"
        },
        Profile().apply {
            profileId = 2
            email = "mail2@wa2.it"
            name = "John"
            surname = "Kerner"
            phoneNumber = "330-551-6835"
            address = "Corso Duca degli Abruzzi, 24"
            city = "Turin"
            country = "Italy"
        },
    )

    fun insertProfiles(profileRepository: ProfileRepository) {
        profileRepository.saveAll(profiles)
    }
}