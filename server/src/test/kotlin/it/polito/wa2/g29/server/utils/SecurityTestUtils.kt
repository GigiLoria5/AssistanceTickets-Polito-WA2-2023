package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.enums.UserType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

object SecurityTestUtils {

    fun setClient(email: String) {
        setCurrentUser(email, UserType.CUSTOMER)
    }

    fun setExpert(email: String) {
        setCurrentUser(email, UserType.EXPERT)
    }

    fun setManager() {
        setCurrentUser("admin@example.com", UserType.MANAGER)
    }

    private fun setCurrentUser(email: String, authority: UserType) {
        val password = "password"
        val authorities = mutableListOf<GrantedAuthority>()
        authorities.add(GrantedAuthority { authority.toString() })
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(email, password, authorities)
    }
}