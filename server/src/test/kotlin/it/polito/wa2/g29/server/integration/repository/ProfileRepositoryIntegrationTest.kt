package it.polito.wa2.g29.server.integration.repository

import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.utils.TestProfileUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ProfileRepositoryIntegrationTest : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var profileRepository: ProfileRepository

    @BeforeEach
    fun setup() {
        profileRepository.deleteAll()
        TestProfileUtils.insertProfiles(profileRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// findProfileByEmail
    /////////////////////////////////////////////////////////////////////

    @Test
    fun findByProfileByEmail() {
        val expectedProfile = profileRepository.findAll()[0]

        val actualProfile = profileRepository.findProfileByEmail(expectedProfile.email)

        assert(actualProfile != null)
        if (actualProfile != null) {
            assert(actualProfile.toDTO() == expectedProfile.toDTO())
        }
    }

    @Test
    fun findByProfileByEmailNotFound() {
        val nonExistingEmail = "odasjo1j29010921jdsa90d1920@fake.org"

        val actualProfile = profileRepository.findProfileByEmail(nonExistingEmail)

        assert(actualProfile == null)
    }

    /////////////////////////////////////////////////////////////////////
    ////// findProfileByPhoneNumber
    /////////////////////////////////////////////////////////////////////

    @Test
    fun findByProfileByPhoneNumber() {
        val expectedProfile = profileRepository.findAll()[0]

        val actualProfile = profileRepository.findProfileByPhoneNumber(expectedProfile.phoneNumber)

        assert(actualProfile != null)
        if (actualProfile != null) {
            assert(actualProfile.toDTO() == expectedProfile.toDTO())
        }
    }

    @Test
    fun findByProfileByPhoneNumberNotFound() {
        val nonExistingEmail = "000-000-0000"

        val actualProfile = profileRepository.findProfileByEmail(nonExistingEmail)

        assert(actualProfile == null)
    }
}