package it.polito.wa2.g29.server.controller

import it.polito.wa2.g29.server.dto.auth.AccessTokenRequestDTO
import it.polito.wa2.g29.server.dto.auth.CreateClientDTO
import it.polito.wa2.g29.server.dto.auth.ErrorResponseDTO
import it.polito.wa2.g29.server.service.AuthService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.http.*
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException

@RequestMapping("/API/auth")
@Validated
@RestController
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid @NotNull request: AccessTokenRequestDTO): ResponseEntity<*> {
        return try {
            val accessToken = authService.authenticateUser(request)
            ResponseEntity(accessToken, HttpStatus.OK)
        } catch (ex: HttpClientErrorException) {
            val error = ErrorResponseDTO("Invalid username or password")
            ResponseEntity(error, ex.statusCode)
        }
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(@RequestBody @Valid @NotNull createClientDTO: CreateClientDTO) {
        authService.addClient(createClientDTO)
    }

}
