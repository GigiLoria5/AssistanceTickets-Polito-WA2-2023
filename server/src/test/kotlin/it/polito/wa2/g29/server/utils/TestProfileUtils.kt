package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ProfileRepository

object TestProfileUtils {
    val profiles = listOf(
        Profile(
            email = "email1@wa2.it",
            name = "Shadow",
            surname = "Deighton",
            phoneNumber = "6462007344",
            address = "4 Pepper Wood Street",
            city = "Humble",
            country = "United States"
        ),
        Profile(
            email = "email2@wa2.it",
            name = "John",
            surname = "Kerner",
            phoneNumber = "3305516835",
            address = "Corso Duca degli Abruzzi, 24",
            city = "Turin",
            country = "Italy"
        ),
    )

    val newProfileDTO = ProfileDTO(
        profileId = null,
        email = "new_mail@test.com",
        name = "Name",
        surname = "Surname",
        phoneNumber = "3333333333",
        address = "New Address",
        city = "New City",
        country = "New Country"
    )

    fun insertProfiles(profileRepository: ProfileRepository) {
        profileRepository.saveAll(profiles)
    }

}