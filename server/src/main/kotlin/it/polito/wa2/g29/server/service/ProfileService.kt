package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.dto.profile.EditProfileDTO

interface ProfileService {
    fun getProfileByEmail(email: String): ProfileDTO

    fun createProfile(profileDTO: ProfileDTO)

    fun modifyProfile(newProfile: EditProfileDTO)
}