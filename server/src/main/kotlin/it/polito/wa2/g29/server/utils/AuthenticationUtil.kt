package it.polito.wa2.g29.server.utils

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component("AuthUtil")
object AuthenticationUtil {
    const val ROLE_CLIENT = "ROLE_CLIENT"
    const val ROLE_EXPERT = "ROLE_EXPERT"
    const val ROLE_MANAGER = "ROLE_MANAGER"
    private fun hasAuthority(authority: String): Boolean {
        val authorities = getAuthContext().authorities.mapNotNull { it.authority }
        return authorities.contains(authority)
    }

    fun isClient(): Boolean = hasAuthority(ROLE_CLIENT)
    fun isExpert(): Boolean = hasAuthority(ROLE_EXPERT)

    fun getUsername(): String {
        return getAuthContext().name
    }

    private fun getAuthContext(): Authentication {
        return SecurityContextHolder.getContext().authentication
    }
}