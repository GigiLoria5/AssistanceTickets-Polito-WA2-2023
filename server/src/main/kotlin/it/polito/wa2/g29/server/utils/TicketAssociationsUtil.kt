package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProfileRepository

class TicketAssociationsUtil(
    private val expertRepository: ExpertRepository,
    private val profileRepository: ProfileRepository
) {
    fun authenticatedUserIsAssociatedWithTicket(ticket: Ticket): Boolean {
        val username = AuthenticationUtil.getUsername()
        return when (AuthenticationUtil.getUserTypeEnum()) {
            UserType.EXPERT -> expertHasTicket(ticket, username)
            UserType.CUSTOMER -> clientHasTicket(ticket, username)
            UserType.MANAGER -> false
        }
    }

    private fun expertHasTicket(ticket: Ticket, username: String): Boolean {
        val expert = expertRepository.findExpertByEmail(username)!!
        return expert.id == ticket.expert?.id
    }

    private fun clientHasTicket(ticket: Ticket, username: String): Boolean {
        val client = profileRepository.findProfileByEmail(username)!!
        return client.id==ticket.customer.id
    }
}