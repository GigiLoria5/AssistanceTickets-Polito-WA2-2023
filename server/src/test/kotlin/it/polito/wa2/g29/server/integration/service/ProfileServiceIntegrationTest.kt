package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.DuplicateProfileException
import it.polito.wa2.g29.server.exception.ProfileNotFoundException
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.ProfileService
import it.polito.wa2.g29.server.utils.TestProductUtils
import it.polito.wa2.g29.server.utils.TestProfileUtils
import it.polito.wa2.g29.server.utils.TestTicketUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ProfileServiceIntegrationTest : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var profileService: ProfileService

    @Autowired
    private lateinit var profileRepository: ProfileRepository

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    lateinit var testProfiles: List<Profile>

    @BeforeEach
    fun setup() {
        profileRepository.deleteAllInBatch()
        testProfiles = TestProfileUtils.insertProfiles(profileRepository)
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
        TestTicketUtils.profiles = testProfiles
        TestTicketUtils.products = TestProductUtils.insertProducts(productRepository)
        val tickets = TestTicketUtils.insertTickets(ticketRepository)
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
    fun modifyProfilePartial() {
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy(
            email = "new_mail@test.com",
            phoneNumber = "3333333333"
        )

        profileService.modifyProfile(oldProfileDTO.email, newProfileDTO)

        assert(profileRepository.findAll().size == testProfiles.size)
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
    @Transactional
    fun modifyProfileComplete() {
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy(
            email = "new_mail@test.com",
            name = "NewName",
            surname = "NewSurname",
            phoneNumber = "333-333-3333",
            address = "NewAddress",
            city = "NewCity",
            country = "NewCountry"
        )

        profileService.modifyProfile(oldProfileDTO.email, newProfileDTO)

        assert(profileRepository.findAll().size == testProfiles.size)
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
    @Transactional
    fun modifyProfileCompleteSameEmail() {
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy(
            name = "NewName",
            surname = "NewSurname",
            phoneNumber = "333-333-3333",
            address = "NewAddress",
            city = "NewCity",
            country = "NewCountry"
        )

        profileService.modifyProfile(oldProfileDTO.email, newProfileDTO)

        assert(profileRepository.findAll().size == testProfiles.size)
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
    @Transactional
    fun modifyProfileCompleteSamePhoneNumber() {
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy(
            email = "new_mail@test.com",
            name = "NewName",
            surname = "NewSurname",
            address = "NewAddress",
            city = "NewCity",
            country = "NewCountry"
        )

        profileService.modifyProfile(oldProfileDTO.email, newProfileDTO)

        assert(profileRepository.findAll().size == testProfiles.size)
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
    @Transactional
    fun modifyProfileCompleteSameEmailAndPhoneNumber() {
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy(
            name = "NewName",
            surname = "NewSurname",
            address = "NewAddress",
            city = "NewCity",
            country = "NewCountry"
        )

        profileService.modifyProfile(oldProfileDTO.email, newProfileDTO)

        assert(profileRepository.findAll().size == testProfiles.size)
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
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy(
            phoneNumber = "333-333-3333"
        )

        assertThrows<ProfileNotFoundException> {
            profileService.modifyProfile("non_existing_email@fake.com", newProfileDTO)
        }
    }

    @Test
    fun modifyProfileDuplicateEmail() {
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy(
            email = testProfiles[1].email
        )

        assertThrows<DuplicateProfileException> {
            profileService.modifyProfile(oldProfileDTO.email, newProfileDTO)
        }
    }

    @Test
    fun modifyProfileDuplicatePhoneNumber() {
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy(
            phoneNumber = testProfiles[1].phoneNumber
        )

        assertThrows<DuplicateProfileException> {
            profileService.modifyProfile(oldProfileDTO.email, newProfileDTO)
        }
    }
}