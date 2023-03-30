package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.dto.ProfilePutDTO
import it.polito.wa2.g29.server.dto.allFieldsAreNullOrBlank
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.DuplicateProfileException
import it.polito.wa2.g29.server.exception.MissingFieldException
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
        if (profileRepository.findProfileByEmail(profileDTO.email.orEmpty()) != null)
            throw DuplicateProfileException("creation of a new profile failed, this email has already been used")
        if (profileRepository.findProfileByPhoneNumber(profileDTO.phoneNumber.orEmpty()) != null)
            throw DuplicateProfileException("creation of a new profile failed, this phone number has already been used")

        val profile = profileDTO.toEntity()
        profileRepository.save(profile)
    }

    override fun modifyProfile(old: ProfileDTO, new: ProfilePutDTO) {
        if (new.allFieldsAreNullOrBlank())
            throw MissingFieldException("nothing to update")

        if (!new.phoneNumber.isNullOrBlank()) {
            if (profileRepository.findProfileByPhoneNumber(new.phoneNumber) != null)
                throw DuplicateProfileException("update of a profile failed, this phone number has already been used")
            old.phoneNumber = new.phoneNumber
        }

        if (!new.name.isNullOrBlank())
            old.name = new.name
        if (!new.surname.isNullOrBlank())
            old.surname = new.surname
        if (!new.address.isNullOrBlank())
            old.address = new.address

        if (!new.city.isNullOrBlank())
            old.city = new.city
        if (!new.country.isNullOrBlank())
            old.country = new.country

        val profile = old.toEntity()
        profileRepository.save(profile)
    }
}