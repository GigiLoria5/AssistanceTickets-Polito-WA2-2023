package it.polito.wa2.g29.server.service.impl

import com.nimbusds.jose.shaded.gson.Gson
import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g29.server.config.KeycloakProperties
import it.polito.wa2.g29.server.dto.auth.AccessTokenRequestDTO
import it.polito.wa2.g29.server.dto.auth.CreateClientDTO
import it.polito.wa2.g29.server.dto.auth.TokenResponseDTO
import it.polito.wa2.g29.server.service.AuthService
import it.polito.wa2.g29.server.service.ProfileService
import it.polito.wa2.g29.server.utils.AuthenticationUtil.KEYCLOAK_ROLE_CLIENT
import it.polito.wa2.g29.server.utils.KeycloakUtil.insertUserInKeycloak
import jakarta.transaction.Transactional
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class AuthServiceImpl(
    private val keycloakProperties: KeycloakProperties,
    private val profileService: ProfileService
) : AuthService {

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

    @Transactional
    @Observed
    override fun addClient(createClientDTO: CreateClientDTO) {
        //it will check that a profile with these email/phone number does not already exist (if exists, it will throw an exception)
        profileService.alreadyExistenceCheck(createClientDTO)

        val email = createClientDTO.email
        val password = createClientDTO.password

        // insert user in keycloak (it will throw an exception if not possible)
        insertUserInKeycloak(keycloakProperties, email, password,KEYCLOAK_ROLE_CLIENT)

        //insert user in our System
        profileService.createProfile(createClientDTO)
    }
}
