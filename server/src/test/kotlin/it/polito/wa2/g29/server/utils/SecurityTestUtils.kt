package it.polito.wa2.g29.server.utils

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

object SecurityTestUtils {

    fun setClient(email: String) {
        setCurrentUser(email, AuthenticationUtil.ROLE_CLIENT)
    }

    fun setExpert(email: String) {
        setCurrentUser(email, AuthenticationUtil.ROLE_EXPERT)
    }

    fun setManager() {
        setCurrentUser("admin@example.com", AuthenticationUtil.ROLE_MANAGER)
    }

    private fun setCurrentUser(email: String, authority: String) {
        val password = "password"
        val authorities = mutableListOf<GrantedAuthority>()
        authorities.add(GrantedAuthority { authority })
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(email, password, authorities)
    }
}