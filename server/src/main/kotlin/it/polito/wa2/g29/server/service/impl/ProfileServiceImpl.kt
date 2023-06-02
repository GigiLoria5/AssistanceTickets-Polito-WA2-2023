package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.dto.auth.CreateClientDTO
import it.polito.wa2.g29.server.dto.profile.EditProfileDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.exception.DuplicateProfileException
import it.polito.wa2.g29.server.exception.ProfileNotFoundException
import it.polito.wa2.g29.server.model.toEntity
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.service.ProfileService
import it.polito.wa2.g29.server.utils.AuthenticationUtil
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val expertRepository: ExpertRepository
) : ProfileService {
    private val log = LoggerFactory.getLogger(ProfileServiceImpl::class.java)

    override fun getProfileByEmail(email: String): ProfileDTO {
        checkUserAuthorisation(email)
        val profile = profileRepository.findProfileByEmail(email)
            ?: run{
                log.info("Profile not found")
                throw ProfileNotFoundException()
            }
        return profile.toDTO()
    }

    override fun alreadyExistenceCheck(createClientDTO: CreateClientDTO) {
        if (profileRepository.findProfileByEmail(createClientDTO.email) != null){
            log.info("a profile with the same email: {} already exists", createClientDTO.email)
            throw DuplicateProfileException("a profile with the same email already exists")
        }
        if (profileRepository.findProfileByPhoneNumber(createClientDTO.phoneNumber) != null){
            log.info("a profile with the same phone number: {} already exists", createClientDTO.phoneNumber)
            throw DuplicateProfileException("a profile with the same phone number already exists")
        }
    }

    override fun createProfile(createClientDTO: CreateClientDTO) {
        val profile = createClientDTO.toEntity()
        profileRepository.save(profile)
    }

    override fun modifyProfile(newProfile: EditProfileDTO) {
        val username = AuthenticationUtil.getUsername()
        val profile = profileRepository.findProfileByEmail(username)!!
        if (newProfile.phoneNumber != profile.phoneNumber && profileRepository.findProfileByPhoneNumber(newProfile.phoneNumber) != null){
            log.info("a profile with the same phone number: {} already exists", newProfile.phoneNumber)
            throw DuplicateProfileException("a profile with the same phone number already exists")
        }
        profile.update(newProfile)
        profileRepository.save(profile)
    }

    private fun checkUserAuthorisation(email: String) {
        val username = AuthenticationUtil.getUsername()

        when (AuthenticationUtil.getUserTypeEnum()) {
            UserType.CUSTOMER -> {
                if (username != email){
                    log.info("Access denied. User: {} cannot access to {} details", username,email)
                    throw AccessDeniedException("")
                }
            }

            UserType.EXPERT -> {
                val expert = expertRepository.findExpertByEmail(username)!!
                val foundCustomer = expert.tickets.any {
                    it.customer.email == email && it.status != TicketStatus.CLOSED
                }
                if (!foundCustomer) {
                    log.info("Access denied.")
                    throw AccessDeniedException("")
                }
            }

            UserType.MANAGER -> Unit
        }
    }

}
