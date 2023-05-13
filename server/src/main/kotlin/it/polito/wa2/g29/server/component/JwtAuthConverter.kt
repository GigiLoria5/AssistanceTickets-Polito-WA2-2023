package it.polito.wa2.g29.server.component

import it.polito.wa2.g29.server.config.JwtAuthConverterProperties
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class JwtAuthConverter(private val properties: JwtAuthConverterProperties) :
    Converter<Jwt, AbstractAuthenticationToken> {

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val authorities = mutableSetOf<GrantedAuthority>()
        val realmAccess = jwt.getClaim("realm_access") as Map<String, Any>?
        val realmRoles = realmAccess?.get("roles") as? Collection<*>
        realmRoles?.forEach { role ->
            authorities.add(SimpleGrantedAuthority("ROLE_$role".uppercase()))
        }
        return JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt))
    }

    private fun getPrincipalClaimName(jwt: Jwt): String {
        return jwt.getClaim(properties.principalAttribute)
    }
    
}
