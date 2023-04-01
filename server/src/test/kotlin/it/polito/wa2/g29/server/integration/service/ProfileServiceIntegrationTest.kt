package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.DuplicateProfileException
import it.polito.wa2.g29.server.exception.ProfileNotFoundException
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.service.ProfileService
import it.polito.wa2.g29.server.utils.TestProfileUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class ProfileServiceIntegrationTest : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var profileService: ProfileService

    @Autowired
    private lateinit var profileRepository: ProfileRepository

    @BeforeEach
    fun setup() {
        profileRepository.deleteAll()
        TestProfileUtils.insertProfiles(profileRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// getProfileByEmail
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getProfileByEmail() {
        val expectedProfileDTO = profileRepository.findAll()[0].toDTO()

        val actualProfileDTO = profileService.getProfileByEmail(expectedProfileDTO.email)

        assert(actualProfileDTO == expectedProfileDTO)
    }

    @Test
    fun getProfileByEmailNotFound() {
        assertThrows<ProfileNotFoundException> {
            profileService.getProfileByEmail("non_existing_email@fake.com")
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// createProfile
    /////////////////////////////////////////////////////////////////////

    @Test
    fun createProfile() {
        val newProfileDTO = TestProfileUtils.profiles[0].toDTO().copy().apply {
            email = "new_mail@test.com"
            phoneNumber = "333-333-3333"
        }

        profileService.createProfile(newProfileDTO)

        assert(profileRepository.findAll().size == (TestProfileUtils.profiles.size + 1))
    }

    @Test
    fun createProfileDuplicateEmail() {
        val newProfileDTO = TestProfileUtils.profiles[0].toDTO().copy().apply {
            phoneNumber = "333-333-3333"
        }

        assertThrows<DuplicateProfileException> {
            profileService.createProfile(newProfileDTO)
        }
    }

    @Test
    fun createProfileDuplicatePhoneNumber() {
        val newProfileDTO = TestProfileUtils.profiles[0].toDTO().copy().apply {
            email = "new_mail@test.com"
        }

        assertThrows<DuplicateProfileException> {
            profileService.createProfile(newProfileDTO)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// modifyProfile
    /////////////////////////////////////////////////////////////////////

    @Test
    fun modifyProfilePartial() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy().apply {
            email = "new_mail@test.com"
            phoneNumber = "333-333-3333"
        }

        profileService.modifyProfile(oldProfileDTO.email, newProfileDTO)

        assert(profileRepository.findAll().size == TestProfileUtils.profiles.size)
        val actualNewProfileDTO = profileService.getProfileByEmail(newProfileDTO.email)
        assert(actualNewProfileDTO.email == newProfileDTO.email)
        assert(actualNewProfileDTO.name == oldProfileDTO.name)
        assert(actualNewProfileDTO.surname == oldProfileDTO.surname)
        assert(actualNewProfileDTO.phoneNumber == newProfileDTO.phoneNumber)
        assert(actualNewProfileDTO.address == oldProfileDTO.address)
        assert(actualNewProfileDTO.city == oldProfileDTO.city)
        assert(actualNewProfileDTO.country == oldProfileDTO.country)
    }

    @Test
    fun modifyProfileComplete() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy().apply {
            email = "new_mail@test.com"
            name = "NewName"
            surname = "NewSurname"
            phoneNumber = "333-333-3333"
            address = "NewAddress"
            city = "NewCity"
            country = "NewCountry"
        }

        profileService.modifyProfile(oldProfileDTO.email, newProfileDTO)

        assert(profileRepository.findAll().size == TestProfileUtils.profiles.size)
        val actualNewProfileDTO = profileService.getProfileByEmail(newProfileDTO.email)
        assert(actualNewProfileDTO.email == newProfileDTO.email)
        assert(actualNewProfileDTO.name == newProfileDTO.name)
        assert(actualNewProfileDTO.surname == newProfileDTO.surname)
        assert(actualNewProfileDTO.phoneNumber == newProfileDTO.phoneNumber)
        assert(actualNewProfileDTO.address == newProfileDTO.address)
        assert(actualNewProfileDTO.city == newProfileDTO.city)
        assert(actualNewProfileDTO.country == newProfileDTO.country)
    }

    @Test
    fun modifyProfileCompleteSameEmail() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy().apply {
            name = "NewName"
            surname = "NewSurname"
            phoneNumber = "333-333-3333"
            address = "NewAddress"
            city = "NewCity"
            country = "NewCountry"
        }

        profileService.modifyProfile(oldProfileDTO.email, newProfileDTO)

        assert(profileRepository.findAll().size == TestProfileUtils.profiles.size)
        val actualNewProfileDTO = profileService.getProfileByEmail(newProfileDTO.email)
        assert(actualNewProfileDTO.email == oldProfileDTO.email)
        assert(actualNewProfileDTO.name == newProfileDTO.name)
        assert(actualNewProfileDTO.surname == newProfileDTO.surname)
        assert(actualNewProfileDTO.phoneNumber == newProfileDTO.phoneNumber)
        assert(actualNewProfileDTO.address == newProfileDTO.address)
        assert(actualNewProfileDTO.city == newProfileDTO.city)
        assert(actualNewProfileDTO.country == newProfileDTO.country)
    }

    @Test
    fun modifyProfileCompleteSamePhoneNumber() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy().apply {
            email = "new_mail@test.com"
            name = "NewName"
            surname = "NewSurname"
            address = "NewAddress"
            city = "NewCity"
            country = "NewCountry"
        }

        profileService.modifyProfile(oldProfileDTO.email, newProfileDTO)

        assert(profileRepository.findAll().size == TestProfileUtils.profiles.size)
        val actualNewProfileDTO = profileService.getProfileByEmail(newProfileDTO.email)
        assert(actualNewProfileDTO.email == newProfileDTO.email)
        assert(actualNewProfileDTO.name == newProfileDTO.name)
        assert(actualNewProfileDTO.surname == newProfileDTO.surname)
        assert(actualNewProfileDTO.phoneNumber == oldProfileDTO.phoneNumber)
        assert(actualNewProfileDTO.address == newProfileDTO.address)
        assert(actualNewProfileDTO.city == newProfileDTO.city)
        assert(actualNewProfileDTO.country == newProfileDTO.country)
    }

    @Test
    fun modifyProfileCompleteSameEmailAndPhoneNumber() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy().apply {
            name = "NewName"
            surname = "NewSurname"
            address = "NewAddress"
            city = "NewCity"
            country = "NewCountry"
        }

        profileService.modifyProfile(oldProfileDTO.email, newProfileDTO)

        assert(profileRepository.findAll().size == TestProfileUtils.profiles.size)
        val actualNewProfileDTO = profileService.getProfileByEmail(newProfileDTO.email)
        assert(actualNewProfileDTO.email == oldProfileDTO.email)
        assert(actualNewProfileDTO.name == newProfileDTO.name)
        assert(actualNewProfileDTO.surname == newProfileDTO.surname)
        assert(actualNewProfileDTO.phoneNumber == oldProfileDTO.phoneNumber)
        assert(actualNewProfileDTO.address == newProfileDTO.address)
        assert(actualNewProfileDTO.city == newProfileDTO.city)
        assert(actualNewProfileDTO.country == newProfileDTO.country)
    }

    @Test
    fun modifyProfileEmailNotFound() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy().apply {
            phoneNumber = "333-333-3333"
        }

        assertThrows<ProfileNotFoundException> {
            profileService.modifyProfile("non_existing_email@fake.com", newProfileDTO)
        }
    }

    @Test
    fun modifyProfileDuplicateEmail() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy().apply {
            email = TestProfileUtils.profiles[1].email
        }

        assertThrows<DuplicateProfileException> {
            profileService.modifyProfile(oldProfileDTO.email, newProfileDTO)
        }
    }

    @Test
    fun modifyProfileDuplicatePhoneNumber() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy().apply {
            phoneNumber = TestProfileUtils.profiles[1].phoneNumber
        }

        assertThrows<DuplicateProfileException> {
            profileService.modifyProfile(oldProfileDTO.email, newProfileDTO)
        }
    }
}