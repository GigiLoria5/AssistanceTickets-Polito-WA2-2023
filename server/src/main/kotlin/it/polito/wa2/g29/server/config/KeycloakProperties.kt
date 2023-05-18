package it.polito.wa2.g29.server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "keycloak")
class KeycloakProperties {
    lateinit var baseUrl: String
    lateinit var realm: String
    lateinit var clientId: String
    lateinit var clientSecret: String
}