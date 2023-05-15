package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.enums.UserType
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
    fun isManager(): Boolean = hasAuthority(ROLE_MANAGER)

    fun getUsername(): String {
        return getAuthContext().name
    }

    fun getUserTypeEnum():UserType{
        return when{
            isClient()->UserType.CUSTOMER
            isExpert()->UserType.EXPERT
            else/*isManager()*/->UserType.MANAGER
        }
    }
    private fun getAuthContext(): Authentication {
        return SecurityContextHolder.getContext().authentication
    }
}