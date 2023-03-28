package it.polito.wa2.g29.server.controller

import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.service.ProfileService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.jetbrains.annotations.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/API")
@Validated
@RestController
class ProfileController(
    private val profileService: ProfileService
) {

    @GetMapping("/profiles/{email}")
    fun getProfileByEmail(@PathVariable @Valid @NotNull @Email email: String): ProfileDTO {
        return profileService.getProfileByEmail(email)
    }
}