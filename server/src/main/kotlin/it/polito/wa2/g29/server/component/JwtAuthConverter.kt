package it.polito.wa2.g29.server.component

import it.polito.wa2.g29.server.config.JwtAuthConverterProperties
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.stereotype.Component

@Component
class JwtAuthConverter(private val properties: JwtAuthConverterProperties) :
    Converter<Jwt, AbstractAuthenticationToken> {

    private val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val authorities: Set<GrantedAuthority> = (jwtGrantedAuthoritiesConverter.convert(jwt) ?: emptyList())
            .plus(extractResourceRoles(jwt))
            .map { SimpleGrantedAuthority("ROLE_$it") }
            .toSet()
        return JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt))
    }

    private fun getPrincipalClaimName(jwt: Jwt): String {
        return jwt.getClaim(properties.principalAttribute)
    }

    private fun extractResourceRoles(jwt: Jwt): Collection<String> {
        val resourceAccess = jwt.getClaim("resource_access") as Map<String, Any>?
        return resourceAccess?.let { resources ->
            val resource = resources[properties.resourceId] as Map<String, Any>?
            val resourceRoles = resource?.get("roles") as Collection<String>?
            resourceRoles ?: emptyList()
        } ?: emptyList()
    }
}