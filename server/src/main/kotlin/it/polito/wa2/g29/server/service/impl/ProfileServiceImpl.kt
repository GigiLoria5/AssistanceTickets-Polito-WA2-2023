package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.ProfileNotFoundException
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

}