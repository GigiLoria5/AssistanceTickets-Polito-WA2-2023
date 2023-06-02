package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.config.KeycloakProperties
import it.polito.wa2.g29.server.exception.DuplicateKeycloakUserException
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation

object KeycloakUtil {

    fun insertUserInKeycloak(
        keycloakProperties: KeycloakProperties,
        email: String,
        password: String,
        userRole: String
    ) {
        // Get realm resource
        val realmResource = getRealmResource(keycloakProperties)

        // insert user in keycloak (it will throw an exception if not possible)
        insertUser(realmResource, email, password)

        //set user Role
        setUserRole(realmResource, email, userRole)
    }

    private fun getRealmResource(keycloakProperties: KeycloakProperties): RealmResource {
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

    private fun manageKeycloakResponseStatus(responseStatus: Int) {
        when (responseStatus) {
            201 -> Unit
            409 -> throw DuplicateKeycloakUserException("a user with the same email already exists")
            else -> throw Exception()
        }
    }

    private fun insertUser(realmResource: RealmResource, email: String, password: String) {
        val credentialRepresentation = createPasswordCredentials(password)
        val user = UserRepresentation()
        user.username = email
        user.email = email
        user.credentials = listOf(credentialRepresentation)
        user.isEmailVerified = true
        user.isEnabled = true
        val userResource = realmResource.users()
        val responseStatus = userResource?.create(user)?.status ?: 500
        manageKeycloakResponseStatus(responseStatus)
    }

    private fun setUserRole(realmResource: RealmResource, email: String, userRole: String) {
        val userId = realmResource
            .users()
            .search(email)[0]
            .id

        val user = realmResource
            .users()[userId]

        val roleToAdd = realmResource
            .roles()[userRole]
            .toRepresentation()

        user.roles().realmLevel().remove(
            user.roles().realmLevel().listAll()
        )

        user.roles().realmLevel().add(listOf(roleToAdd))
    }

}
