package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.dto.profile.EditProfileDTO
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ProfileRepository

object ProfileTestUtils {

    /**
     * Inserts a list of new profiles into the provided [profileRepository] and returns an array of the newly added profiles.
     * @param profileRepository the repository where the profiles should be saved
     * @return an array of the newly added profiles, with a guaranteed size of 2
     */
    fun insertProfiles(profileRepository: ProfileRepository): List<Profile> {
        val newProfiles = getProfiles()
        profileRepository.saveAll(newProfiles)
        return newProfiles
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
        )
    }

    fun getNewEditProfileDTO(): EditProfileDTO {
        return EditProfileDTO(
            name = "Name",
            surname = "Surname",
            phoneNumber = "3333333333",
            address = "New Address",
            city = "New City",
            country = "New Country"
        )
    }

    fun generateRandomPhoneNumber(): String {
        val timestamp = System.currentTimeMillis()
        return (timestamp % 10000000000).toString().padStart(10, '0')
    }

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

    private fun getProfiles(): List<Profile> {
        return profiles.map { Profile(it.email, it.name, it.surname, it.phoneNumber, it.address, it.city, it.country) }
    }

}
