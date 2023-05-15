package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.dto.profile.EditProfileDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.exception.DuplicateProfileException
import it.polito.wa2.g29.server.exception.ProfileNotFoundException
import it.polito.wa2.g29.server.model.toEntity
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.service.ProfileService
import org.springframework.security.access.AccessDeniedException
import it.polito.wa2.g29.server.utils.AuthenticationUtil
import org.springframework.stereotype.Service

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val expertRepository: ExpertRepository
) : ProfileService {

    override fun getProfileByEmail(email: String): ProfileDTO {
        val username = AuthenticationUtil.getUsername()
        if (
            AuthenticationUtil.isClient() &&
            username != email
        )
            throw AccessDeniedException("")

        if (AuthenticationUtil.isExpert()) {
            val expert = expertRepository.findExpertByEmail(username)!!
            val foundCustomer = expert.tickets.any {
                it.customer.email == email && it.status != TicketStatus.CLOSED
            }
            if (!foundCustomer)
                throw AccessDeniedException("")
        }

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

    override fun modifyProfile(email: String, newProfile: EditProfileDTO) {
        val profile = profileRepository.findProfileByEmail(email) ?: throw ProfileNotFoundException()
        if (newProfile.phoneNumber != profile.phoneNumber && profileRepository.findProfileByPhoneNumber(newProfile.phoneNumber) != null)
            throw DuplicateProfileException("a profile with the same phone number already exists")

        profile.update(newProfile)
        profileRepository.save(profile)
    }

}
