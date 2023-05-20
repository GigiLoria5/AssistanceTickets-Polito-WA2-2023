package it.polito.wa2.g29.server.service.impl

import com.nimbusds.jose.shaded.gson.Gson
import it.polito.wa2.g29.server.config.KeycloakProperties
import it.polito.wa2.g29.server.dto.auth.AccessTokenRequestDTO
import it.polito.wa2.g29.server.dto.auth.CreateClientDTO
import it.polito.wa2.g29.server.dto.auth.TokenResponseDTO
import it.polito.wa2.g29.server.service.AuthService
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class AuthServiceImpl(private val keycloakProperties: KeycloakProperties) : AuthService {

    override fun authenticateUser(request: AccessTokenRequestDTO): TokenResponseDTO {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val body = LinkedMultiValueMap<String, String>()
        body.add("client_id", keycloakProperties.clientId)
        body.add("username", request.username)
        body.add("password", request.password)
        body.add("client_secret", keycloakProperties.clientSecret)
        body.add("grant_type", "password")
        val requestEntity = HttpEntity(body, headers)
        val url = "${keycloakProperties.baseUrl}/realms/${keycloakProperties.realm}/protocol/openid-connect/token"
        val gson = Gson()
        val response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String::class.java)
        return gson.fromJson(response.body, TokenResponseDTO::class.java)
    }

    override fun addClient(createClientDTO: CreateClientDTO) {
        // TODO: to be completed
        val realm = keycloakProperties.realm
        // Define user
        val credentialRepresentation = createPasswordCredentials(createClientDTO.password)
        val user = UserRepresentation()
        user.username = createClientDTO.email
        user.firstName = createClientDTO.name
        user.lastName = createClientDTO.surname
        user.email = createClientDTO.email
        user.credentials = listOf(credentialRepresentation)
        user.isEmailVerified = true
        // Get realm
        val keycloak = KeycloakBuilder.builder()
            .realm(keycloakProperties.realm)
            .serverUrl(keycloakProperties.baseUrl)
            .clientId(keycloakProperties.clientId)
            .clientSecret(keycloakProperties.clientSecret)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .build()
        val userResource = keycloak.realm(realm)?.users()
        // Create user
        userResource?.create(user)
    }

    private fun createPasswordCredentials(password: String): CredentialRepresentation {
        val passwordCredentials = CredentialRepresentation()
        passwordCredentials.isTemporary = false
        passwordCredentials.type = CredentialRepresentation.PASSWORD
        passwordCredentials.value = password
        return passwordCredentials
    }

}
