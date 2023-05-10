package it.polito.wa2.g29.server.controller

import it.polito.wa2.g29.server.config.KeycloakProperties
import it.polito.wa2.g29.server.utils.AccessTokenRequest
import it.polito.wa2.g29.server.utils.TokenResponse
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController
class KeycloakController(private val keycloakProperties: KeycloakProperties) {
    @PostMapping("/auth/login")
    fun login(@RequestBody request: AccessTokenRequest): ResponseEntity<TokenResponse> {
        val restTemplate = RestTemplate()

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val body = LinkedMultiValueMap<String, String>()
        body.add("client_id", keycloakProperties.clientId)
        body.add("username", request.username)
        body.add("password", request.password)
        body.add("grant_type", "password")

        val requestEntity = HttpEntity(body, headers)
        val url = "${keycloakProperties.baseUrl}/realms/${keycloakProperties.realm}/protocol/openid-connect/token"

        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, TokenResponse::class.java)

    }
}