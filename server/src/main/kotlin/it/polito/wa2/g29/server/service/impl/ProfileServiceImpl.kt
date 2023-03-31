package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.DuplicateProfileException
import it.polito.wa2.g29.server.exception.ProfileNotFoundException
import it.polito.wa2.g29.server.model.toEntity
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.service.ProfileService
import org.springframework.stereotype.Service

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository
) : ProfileService{
    override fun getProfileByEmail(email: String): ProfileDTO {
        val profile = profileRepository.findProfileByEmail(email) ?: throw ProfileNotFoundException()
        return profile.toDTO()
    }

    override fun createProfile(profileDTO: ProfileDTO) {
        if (profileRepository.findProfileByEmail(profileDTO.email) != null)
            throw DuplicateProfileException("a profile with the same email already exists")
        if (profileRepository.findProfileByPhoneNumber(profileDTO.phoneNumber) != null)
            throw DuplicateProfileException("a profile with the same phone number already exists")

        val profile = profileDTO.toEntity()
        profileRepository.save(profile)
    }

    override fun modifyProfile(email: String, newProfile: ProfileDTO) {

        val oldProfile = profileRepository.findProfileByEmail(email) ?: throw ProfileNotFoundException()

        if (newProfile.email != oldProfile.email && profileRepository.findProfileByEmail(newProfile.email) != null)
            throw DuplicateProfileException("a profile with the same email already exists")
        if (newProfile.phoneNumber != oldProfile.phoneNumber && profileRepository.findProfileByPhoneNumber(newProfile.phoneNumber) != null)
            throw DuplicateProfileException("a profile with the same phone number already exists")

        newProfile.profileId = oldProfile.profileId

        val profile = newProfile.toEntity()
        profileRepository.save(profile)

    }
}