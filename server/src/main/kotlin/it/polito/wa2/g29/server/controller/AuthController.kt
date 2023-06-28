package it.polito.wa2.g29.server.controller

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g29.server.dto.UserDTO
import it.polito.wa2.g29.server.dto.auth.AccessTokenRequestDTO
import it.polito.wa2.g29.server.dto.auth.CreateClientDTO
import it.polito.wa2.g29.server.dto.auth.ErrorResponseDTO
import it.polito.wa2.g29.server.service.AuthService
import it.polito.wa2.g29.server.utils.KeycloakUtil.convertRoleString
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException

@RequestMapping("/API/auth")
@Validated
@RestController
@Observed()
class AuthController(private val authService: AuthService) {

    private val log = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/login")
    fun login(@RequestBody @Valid @NotNull request: AccessTokenRequestDTO): ResponseEntity<*> {
        return try {
            val accessToken = authService.authenticateUser(request)
            ResponseEntity(accessToken, HttpStatus.OK)
        } catch (ex: HttpClientErrorException) {
            log.error("Exception while logging user: {}, {}", request.username, ex.message)
            val error = ErrorResponseDTO("Invalid username or password")
            ResponseEntity(error, ex.statusCode)
        }
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(@RequestBody @Valid @NotNull createClientDTO: CreateClientDTO) {
        authService.addClient(createClientDTO)
    }

    @GetMapping("/user")
    fun user(@RequestHeader(name = "Authorization") token: String): UserDTO {
        val authentication = SecurityContextHolder.getContext().authentication
        val jwtToken = authentication.credentials as Jwt
        val authority = authentication.authorities.toList()[0].authority
        return UserDTO(
            email = jwtToken.getClaim("email"),
            name = jwtToken.getClaim("name"),
            role = convertRoleString(authority)
        )
    }

}
