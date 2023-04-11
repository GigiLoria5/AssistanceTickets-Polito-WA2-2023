package it.polito.wa2.g29.server.controller

import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.service.ProfileService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/API")
@Validated
@RestController
class ProfileController(
    private val profileService: ProfileService
) {
    // GET /API/profiles/{email} -- details of profiles {email} or fail if it does not exist
    @GetMapping("/profiles/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun getProfileByEmail(@PathVariable @NotBlank @Email @Pattern(regexp = ProfileDTO.EMAIL_PATTERN) email: String): ProfileDTO {
        return profileService.getProfileByEmail(email)
    }

    // POST /API/profiles -- create a new profile or fail if some field is missing, or is not valid, or in case of duplicates
    @PostMapping("/profiles")
    @ResponseStatus(HttpStatus.CREATED)
    fun createProfile(@RequestBody @Valid @NotNull profile: ProfileDTO) {
        profileService.createProfile(profile)
    }

    // PUT /API/profiles/{email} -- modify a user profile {email} or fail if it does not exist
    @PutMapping("/profiles/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun modifyProfile(
        @RequestBody @Valid @NotNull newProfile: ProfileDTO,
        @PathVariable @NotBlank @Email @Pattern(regexp = ProfileDTO.EMAIL_PATTERN) email: String
    ) {
        profileService.modifyProfile(email, newProfile)
    }
}