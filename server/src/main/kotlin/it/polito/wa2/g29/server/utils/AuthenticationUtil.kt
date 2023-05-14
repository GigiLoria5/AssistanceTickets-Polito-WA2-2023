package it.polito.wa2.g29.server.utils

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

object AuthenticationUtil {

    private fun hasAuthority(authority: String): Boolean {
        val authorities = getAuthContext().authorities.mapNotNull { it.authority }
        return authorities.contains(authority)
    }

    fun isClient():Boolean= hasAuthority("ROLE_CLIENT")
    fun isExpert():Boolean= hasAuthority("ROLE_EXPERT")
    fun isManager():Boolean= hasAuthority("ROLE_MANAGER")

    fun getUsername(): String {
        return getAuthContext().name
    }

    private fun getAuthContext(): Authentication {
        return SecurityContextHolder.getContext().authentication
    }
}