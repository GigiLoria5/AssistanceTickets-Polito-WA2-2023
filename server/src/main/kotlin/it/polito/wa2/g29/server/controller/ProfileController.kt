package it.polito.wa2.g29.server.controller

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g29.server.dto.ProfileDTO
import it.polito.wa2.g29.server.dto.profile.EditProfileDTO
import it.polito.wa2.g29.server.service.ProfileService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RequestMapping("/API")
@Validated
@RestController
@Observed
class ProfileController(private val profileService: ProfileService) {

    private val log = LoggerFactory.getLogger(ProfileController::class.java)

    @GetMapping("/profiles/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun getProfileByEmail(@PathVariable @NotBlank @Email @Pattern(regexp = ProfileDTO.EMAIL_PATTERN) email: String): ProfileDTO {
        log.info("Retrieve details of profile:{}", email)
        return profileService.getProfileByEmail(email)

    }

    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_CLIENT)")
    @PutMapping("/profiles")
    @ResponseStatus(HttpStatus.OK)
    fun modifyProfile(@RequestBody @Valid @NotNull newProfile: EditProfileDTO) {
        profileService.modifyProfile(newProfile)
    }

}
