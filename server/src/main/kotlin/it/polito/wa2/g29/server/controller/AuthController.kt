package it.polito.wa2.g29.server.controller

import com.nimbusds.jose.shaded.gson.Gson
import it.polito.wa2.g29.server.config.KeycloakProperties
import it.polito.wa2.g29.server.dto.auth.AccessTokenRequestDTO
import it.polito.wa2.g29.server.dto.auth.ErrorResponseDTO
import it.polito.wa2.g29.server.dto.auth.TokenResponseDTO
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController
class AuthController(private val keycloakProperties: KeycloakProperties) {
    @PostMapping("/API/auth/login")
    fun login(@RequestBody request: AccessTokenRequestDTO): ResponseEntity<*> {
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
        val response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String::class.java)
        val gson = Gson()
        return when (response.statusCode) {
            HttpStatus.OK -> {
                val accessToken = gson.fromJson(response.body, TokenResponseDTO::class.java)
                return ResponseEntity.ok(accessToken)
            }

            HttpStatus.UNAUTHORIZED, HttpStatus.BAD_REQUEST -> {
                val error = ErrorResponseDTO("Invalid username or password")
                ResponseEntity.status(response.statusCode).body(error)
            }

            else -> {
                val error = ErrorResponseDTO("Unexpected error occurred")
                ResponseEntity.status(response.statusCode).body(error)
            }
        }
    }
}