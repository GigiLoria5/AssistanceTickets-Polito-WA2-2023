package it.polito.wa2.g29.server.integration.repository

import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.utils.ProfileTestUtils
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfileRepositoryIT : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var profileRepository: ProfileRepository

    lateinit var testProfiles: List<Profile>

    @BeforeAll
    fun setup() {
        testProfiles = ProfileTestUtils.insertProfiles(profileRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// findProfileByEmail
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    fun findByProfileByEmail() {
        val expectedProfile = testProfiles[0]

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
    @Transactional
    fun findByProfileByPhoneNumber() {
        val expectedProfile = testProfiles[0]

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