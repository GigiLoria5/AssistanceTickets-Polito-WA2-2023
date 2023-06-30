package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.auth.CreateClientDTO
import it.polito.wa2.g29.server.dto.profile.EditProfileDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.DuplicateProfileException
import it.polito.wa2.g29.server.exception.ProfileNotFoundException
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.ProfileService
import it.polito.wa2.g29.server.utils.*
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
    private lateinit var expertRepository: ExpertRepository

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    lateinit var testProfiles: List<Profile>

    lateinit var testProducts: List<Product>

    lateinit var testExperts: List<Expert>

    @BeforeAll
    fun setup() {
        testProfiles = ProfileTestUtils.insertProfiles(profileRepository)
        testExperts = ExpertTestUtils.insertExperts(expertRepository)
        testProducts = ProductTestUtils.insertProducts(productRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// getProfileByEmail
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    fun getProfileByEmailWithoutTickets() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val expectedProfileDTO = testProfiles[0].toDTO()

        val actualProfileDTO = profileService.getProfileByEmail(expectedProfileDTO.email)

        assert(actualProfileDTO == expectedProfileDTO)
    }

    @Test
    @Transactional
    @Rollback
    fun getProfileByEmailWithTicketsClient() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        TicketTestUtils.profiles = testProfiles
        TicketTestUtils.products = testProducts
        val tickets = TicketTestUtils.insertTickets(ticketRepository)
        testProfiles[0].tickets.add(tickets.first { it.customer.id == testProfiles[0].id })
        profileRepository.save(testProfiles[0])
        val expectedProfileDTO = testProfiles[0].toDTO()

        val actualProfileDTO = profileService.getProfileByEmail(expectedProfileDTO.email)

        assert(actualProfileDTO == expectedProfileDTO)
    }

    @Test
    fun getProfileByEmailNotFound() {
        SecurityTestUtils.setManager()
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
        val profile = testProfiles[0].toDTO()
        val newProfileDTO = CreateClientDTO(
            email = "new_mail@test.com",
            password = "123",
            name = profile.name,
            surname = profile.surname,
            phoneNumber = "3333333333",
            address = profile.address,
            city = profile.city,
            country = profile.country
        )

        profileService.createProfile(newProfileDTO)

        assert(profileRepository.findAll().size == (testProfiles.size + 1))
    }

    /////////////////////////////////////////////////////////////////////
    ////// modifyProfile
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun modifyProfilePartial() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = EditProfileDTO(
            oldProfileDTO.name,
            oldProfileDTO.surname,
            phoneNumber = "3333333333",
            oldProfileDTO.address,
            oldProfileDTO.city,
            oldProfileDTO.country
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
        SecurityTestUtils.setClient(testProfiles[0].email)
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = EditProfileDTO(
            name = "NewName",
            surname = "NewSurname",
            phoneNumber = "333-333-3333",
            address = "NewAddress",
            city = "NewCity",
            country = "NewCountry"
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
        SecurityTestUtils.setClient(testProfiles[0].email)
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = EditProfileDTO(
            name = "NewName",
            surname = "NewSurname",
            phoneNumber = oldProfileDTO.phoneNumber,
            address = "NewAddress",
            city = "NewCity",
            country = "NewCountry"
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
        SecurityTestUtils.setClient(testProfiles[0].email)
        val newProfileDTO = EditProfileDTO(
            name = "NewName",
            surname = "NewSurname",
            phoneNumber = testProfiles[1].phoneNumber,
            address = "NewAddress",
            city = "NewCity",
            country = "NewCountry"
        )

        assertThrows<DuplicateProfileException> {
            profileService.modifyProfile(newProfileDTO)
        }
    }
}
