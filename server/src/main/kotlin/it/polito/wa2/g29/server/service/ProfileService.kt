package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.dto.TicketDTO
import it.polito.wa2.g29.server.dto.auth.CreateClientDTO
import it.polito.wa2.g29.server.dto.profile.EditProfileDTO

interface ProfileService {
    fun getProfileByEmail(email: String): ProfileDTO

    fun getTicketsOfProfileByEmail(email: String):List<TicketDTO>

    fun getProfileById(profileId: Int): ProfileDTO

    fun createProfile(createClientDTO: CreateClientDTO)

    fun alreadyExistenceCheck(createClientDTO: CreateClientDTO)

    fun modifyProfile(newProfile: EditProfileDTO)
}
