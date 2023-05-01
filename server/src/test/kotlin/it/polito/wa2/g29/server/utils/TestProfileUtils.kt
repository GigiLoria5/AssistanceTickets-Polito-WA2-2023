package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ProfileRepository

object TestProfileUtils {

    private val profiles = listOf(
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

    fun getProfiles(): List<Profile> {
        return profiles.map { Profile(it.email, it.name, it.surname, it.phoneNumber, it.address, it.city, it.country) }
    }

    fun getNewProfileDTO(): ProfileDTO {
        return ProfileDTO(
            profileId = null,
            email = "new_mail@test.com",
            name = "Name",
            surname = "Surname",
            phoneNumber = "3333333333",
            address = "New Address",
            city = "New City",
            country = "New Country",
            ticketsIds = null
        )
    }

    fun insertProfiles(profileRepository: ProfileRepository): List<Profile> {
        val newProfiles = getProfiles()
        profileRepository.saveAll(newProfiles)
        return newProfiles
    }

}