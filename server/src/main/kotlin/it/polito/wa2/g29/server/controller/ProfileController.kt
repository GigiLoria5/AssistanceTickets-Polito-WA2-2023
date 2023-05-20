package it.polito.wa2.g29.server.controller

import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.dto.profile.EditProfileDTO
import it.polito.wa2.g29.server.service.ProfileService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RequestMapping("/API")
@Validated
@RestController
class ProfileController(private val profileService: ProfileService) {

    // GET /API/profiles/{email} -- details of profiles {email} or fail if it does not exist
    @GetMapping("/profiles/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun getProfileByEmail(@PathVariable @NotBlank @Email @Pattern(regexp = ProfileDTO.EMAIL_PATTERN) email: String): ProfileDTO {
        return profileService.getProfileByEmail(email)
    }

    // PUT /API/profiles -- modify a user profile
    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_CLIENT)")
    @PutMapping("/profiles")
    @ResponseStatus(HttpStatus.OK)
    fun modifyProfile(@RequestBody @Valid @NotNull newProfile: EditProfileDTO) {
        profileService.modifyProfile(newProfile)
    }

}
