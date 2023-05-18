package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.model.Ticket
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component("AuthUtil")
object AuthenticationUtil {
    const val ROLE_CLIENT = "ROLE_CLIENT"
    const val ROLE_EXPERT = "ROLE_EXPERT"
    const val ROLE_MANAGER = "ROLE_MANAGER"

    fun getUsername(): String {
        return getAuthContext().name
    }

    fun getUserTypeEnum(): UserType {
        return when {
            isClient() -> UserType.CUSTOMER
            isExpert() -> UserType.EXPERT
            else/*isManager()*/ -> UserType.MANAGER
        }
    }

    private fun hasAuthority(authority: String): Boolean {
        val authorities = getAuthContext().authorities.mapNotNull { it.authority }
        return authorities.contains(authority)
    }

    private fun isClient(): Boolean = hasAuthority(ROLE_CLIENT)
    private fun isExpert(): Boolean = hasAuthority(ROLE_EXPERT)

    //isManager is currently not needed

    fun authenticatedUserIsAssociatedWithTicket(
        ticket: Ticket,
        findExpertByEmail: (String) -> Expert,
        findProfileByEmail: (String) -> Profile
    ): Boolean {
        val username = getUsername()
        return when (getUserTypeEnum()) {
            UserType.EXPERT -> expertHasTicket(ticket, findExpertByEmail(username))
            UserType.CUSTOMER -> clientHasTicket(ticket, findProfileByEmail(username))
            UserType.MANAGER -> false
        }
    }

    private fun getAuthContext(): Authentication {
        return SecurityContextHolder.getContext().authentication
    }

    private fun expertHasTicket(ticket: Ticket, expert: Expert): Boolean {
        return expert.id == ticket.expert?.id
    }

    private fun clientHasTicket(ticket: Ticket, client: Profile): Boolean {
        return client.id == ticket.customer.id
    }
}