package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.profile.EditProfileDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.DuplicateProfileException
import it.polito.wa2.g29.server.exception.ProfileNotFoundException
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.ProfileService
import it.polito.wa2.g29.server.utils.ProductTestUtils
import it.polito.wa2.g29.server.utils.ProfileTestUtils
import it.polito.wa2.g29.server.utils.TicketTestUtils
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfileServiceIT : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var profileService: ProfileService

    @Autowired
    private lateinit var profileRepository: ProfileRepository

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    lateinit var testProfiles: List<Profile>

    @BeforeAll
    fun setup() {
        testProfiles = ProfileTestUtils.insertProfiles(profileRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// getProfileByEmail
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    fun getProfileByEmailWithoutTickets() {
        val expectedProfileDTO = testProfiles[0].toDTO()

        val actualProfileDTO = profileService.getProfileByEmail(expectedProfileDTO.email)

        assert(actualProfileDTO.ticketsIds?.isEmpty() ?: false)
        assert(actualProfileDTO == expectedProfileDTO)
    }

    @Test
    @Transactional
    fun getProfileByEmailWithTickets() {
        TicketTestUtils.profiles = testProfiles
        TicketTestUtils.products = ProductTestUtils.insertProducts(productRepository)
        val tickets = TicketTestUtils.insertTickets(ticketRepository)
        testProfiles[0].tickets.add(tickets.first { it.customer.id == testProfiles[0].id })
        profileRepository.save(testProfiles[0])
        val expectedProfileDTO = testProfiles[0].toDTO()

        val actualProfileDTO = profileService.getProfileByEmail(expectedProfileDTO.email)

        assert(actualProfileDTO.ticketsIds?.isNotEmpty() ?: false)
        assert(actualProfileDTO.ticketsIds?.size == expectedProfileDTO.ticketsIds?.size)
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
    @Transactional
    @Rollback
    fun createProfile() {
        val newProfileDTO = testProfiles[0].toDTO().copy(
            profileId = null,
            email = "new_mail@test.com",
            phoneNumber = "3333333333"
        )

        profileService.createProfile(newProfileDTO)

        assert(profileRepository.findAll().size == (testProfiles.size + 1))
    }

    @Test
    fun createProfileDuplicateEmail() {
        val newProfileDTO = testProfiles[0].toDTO().copy(
            phoneNumber = "3333333333"
        )

        assertThrows<DuplicateProfileException> {
            profileService.createProfile(newProfileDTO)
        }
    }

    @Test
    fun createProfileDuplicatePhoneNumber() {
        val newProfileDTO = testProfiles[0].toDTO().copy(
            email = "new_mail@test.com"
        )

        assertThrows<DuplicateProfileException> {
            profileService.createProfile(newProfileDTO)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// modifyProfile
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun modifyProfilePartial() {
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = EditProfileDTO(
            oldProfileDTO.name,
            oldProfileDTO.surname,
            phoneNumber = "3333333333",
            oldProfileDTO.address,
            oldProfileDTO.city,
            oldProfileDTO.country,
            null
        )

        profileService.modifyProfile(newProfileDTO)

        assert(profileRepository.findAll().size == testProfiles.size)
        val actualNewProfileDTO = profileService.getProfileByEmail(oldProfileDTO.email)
        assert(actualNewProfileDTO.name == oldProfileDTO.name)
        assert(actualNewProfileDTO.surname == oldProfileDTO.surname)
        assert(actualNewProfileDTO.phoneNumber == newProfileDTO.phoneNumber)
        assert(actualNewProfileDTO.address == oldProfileDTO.address)
        assert(actualNewProfileDTO.city == oldProfileDTO.city)
        assert(actualNewProfileDTO.country == oldProfileDTO.country)
    }

    @Test
    @Transactional
    @Rollback
    fun modifyProfileComplete() {
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = EditProfileDTO(
            name = "NewName",
            surname = "NewSurname",
            phoneNumber = "333-333-3333",
            address = "NewAddress",
            city = "NewCity",
            country = "NewCountry",
            null
        )

        profileService.modifyProfile(newProfileDTO)

        assert(profileRepository.findAll().size == testProfiles.size)
        val actualNewProfileDTO = profileService.getProfileByEmail(oldProfileDTO.email)
        assert(actualNewProfileDTO.name == newProfileDTO.name)
        assert(actualNewProfileDTO.surname == newProfileDTO.surname)
        assert(actualNewProfileDTO.phoneNumber == newProfileDTO.phoneNumber)
        assert(actualNewProfileDTO.address == newProfileDTO.address)
        assert(actualNewProfileDTO.city == newProfileDTO.city)
        assert(actualNewProfileDTO.country == newProfileDTO.country)
    }

    @Test
    @Transactional
    @Rollback
    fun modifyProfileCompleteSamePhoneNumber() {
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = EditProfileDTO(
            name = "NewName",
            surname = "NewSurname",
            phoneNumber = oldProfileDTO.phoneNumber,
            address = "NewAddress",
            city = "NewCity",
            country = "NewCountry",
            null
        )

        profileService.modifyProfile(newProfileDTO)

        assert(profileRepository.findAll().size == testProfiles.size)
        val actualNewProfileDTO = profileService.getProfileByEmail(oldProfileDTO.email)
        assert(actualNewProfileDTO.name == newProfileDTO.name)
        assert(actualNewProfileDTO.surname == newProfileDTO.surname)
        assert(actualNewProfileDTO.phoneNumber == oldProfileDTO.phoneNumber)
        assert(actualNewProfileDTO.address == newProfileDTO.address)
        assert(actualNewProfileDTO.city == newProfileDTO.city)
        assert(actualNewProfileDTO.country == newProfileDTO.country)
    }

    @Test
    fun modifyProfileDuplicatePhoneNumber() {
        val newProfileDTO = EditProfileDTO(
            name = "NewName",
            surname = "NewSurname",
            phoneNumber = testProfiles[1].phoneNumber,
            address = "NewAddress",
            city = "NewCity",
            country = "NewCountry",
            null
        )

        assertThrows<DuplicateProfileException> {
            profileService.modifyProfile(newProfileDTO)
        }
    }
}