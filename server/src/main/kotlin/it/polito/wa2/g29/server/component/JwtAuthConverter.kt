package it.polito.wa2.g29.server.component

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class JwtAuthConverter() : Converter<Jwt, AbstractAuthenticationToken> {

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val username = extractPrincipalNameFromJwt(jwt)
        val authorities = extractAuthoritiesFromJwt(jwt)
        return JwtAuthenticationToken(jwt, authorities, username)
    }

    private fun extractPrincipalNameFromJwt(jwt: Jwt): String {
        return jwt.getClaim("email")
    }

    private fun extractAuthoritiesFromJwt(jwt: Jwt): MutableSet<GrantedAuthority> {
        val authorities = mutableSetOf<GrantedAuthority>()
        val realmAccess = jwt.getClaim("realm_access") as Map<String, Any>?
        val realmRoles = realmAccess?.get("roles") as? Collection<*>
        realmRoles?.forEach { role ->
            authorities.add(SimpleGrantedAuthority("ROLE_$role".uppercase()))
        }
        return authorities
    }

}
