package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProfileRepository

class TicketAssociationsUtil(
    private val expertRepository: ExpertRepository,
    private val profileRepository: ProfileRepository
) {
    fun authenticatedUserIsAssociatedWithTicket(ticketId: Int): Boolean {
        val username = AuthenticationUtil.getUsername()
        return when (AuthenticationUtil.getUserTypeEnum()) {
            UserType.EXPERT -> expertHasTicket(ticketId, username)
            UserType.CUSTOMER -> clientHasTicket(ticketId, username)
            UserType.MANAGER -> false
        }
    }

    private fun expertHasTicket(ticketId: Int, username: String): Boolean {
        val expert = expertRepository.findExpertByEmail(username)!!
        return expert.tickets.any {
            it.id == ticketId
        }
    }

    private fun clientHasTicket(ticketId: Int, username: String): Boolean {
        val client = profileRepository.findProfileByEmail(username)!!
        return client.tickets.any {
            it.id == ticketId
        }
    }
}