package it.polito.wa2.g29.server.service.impl

import com.nimbusds.jose.shaded.gson.Gson
import it.polito.wa2.g29.server.config.KeycloakProperties
import it.polito.wa2.g29.server.dto.auth.AccessTokenRequestDTO
import it.polito.wa2.g29.server.dto.auth.CreateClientDTO
import it.polito.wa2.g29.server.dto.auth.TokenResponseDTO
import it.polito.wa2.g29.server.service.AuthService
import it.polito.wa2.g29.server.service.ProfileService
import it.polito.wa2.g29.server.utils.AuthenticationUtil.KEYCLOAK_ROLE_CLIENT
import jakarta.transaction.Transactional
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.RealmResource
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
    override fun addClient(createClientDTO: CreateClientDTO) {
        //it will check that the client's email/phone number does not already exist in our system
        profileService.createProfile(createClientDTO)
        // Get keycloak instance
        val keycloakResources = getKeycloakResources()
        // insert user in keycloak
        insertUserInKeycloak(keycloakResources,createClientDTO.email,createClientDTO.password)
        //define user Role
        setKeycloakUserRole(keycloakResources, createClientDTO.email, KEYCLOAK_ROLE_CLIENT)
    }

    private fun getKeycloakResources(): RealmResource {
        // Get realm
        val realm = keycloakProperties.realm

        return KeycloakBuilder.builder()
            .realm(keycloakProperties.realm)
            .serverUrl(keycloakProperties.baseUrl)
            .clientId(keycloakProperties.clientId)
            .clientSecret(keycloakProperties.clientSecret)
            .grantType(OAuth2Constants.PASSWORD)
            .username(keycloakProperties.signupAdminUsername)
            .password(keycloakProperties.signupAdminPassword)
            .resteasyClient(
                ResteasyClientBuilderImpl()
                    .connectionPoolSize(10)
                    .build()
            )
            .build().realm(realm)
    }
    private fun createPasswordCredentials(password: String): CredentialRepresentation {
        val passwordCredentials = CredentialRepresentation()
        passwordCredentials.isTemporary = false
        passwordCredentials.type = CredentialRepresentation.PASSWORD
        passwordCredentials.value = password
        return passwordCredentials
    }
    private fun insertUserInKeycloak(keycloakResources: RealmResource, email: String, password: String) {
        val credentialRepresentation = createPasswordCredentials(password)
        val user = UserRepresentation()
        user.username = email
        user.email = email
        user.credentials = listOf(credentialRepresentation)
        user.isEmailVerified = true
        user.isEnabled = true
        val userResource = keycloakResources.users()
        userResource?.create(user)
    }
    private fun setKeycloakUserRole(keycloakResources: RealmResource, email: String, userRole: String) {

        val userId = keycloakResources
            .users()
            .search(email)[0]
            .id

        val user = keycloakResources
            .users()[userId]

        val roleToAdd = keycloakResources
            .roles()[userRole]
            .toRepresentation()

        user.roles().realmLevel().remove(
            user.roles().realmLevel().listAll()
        )

        user.roles().realmLevel().add(listOf(roleToAdd))
    }
}
