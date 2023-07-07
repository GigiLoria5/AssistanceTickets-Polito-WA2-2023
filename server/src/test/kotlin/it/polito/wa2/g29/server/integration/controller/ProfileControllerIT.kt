package it.polito.wa2.g29.server.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g29.server.dto.profile.EditProfileDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.utils.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfileControllerIT : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var profileRepository: ProfileRepository

    @Autowired
    private lateinit var expertRepository: ExpertRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    lateinit var testProfiles: List<Profile>

    lateinit var testExperts: List<Expert>

    lateinit var testProducts: List<Product>

    @BeforeAll
    fun setup() {
        testProfiles = ProfileTestUtils.insertProfiles(profileRepository)
        testExperts = ExpertTestUtils.insertExperts(expertRepository)
        testProducts = ProductTestUtils.insertProducts(productRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/profiles/{profileId}
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getProfileWithoutTickets() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val expectedProfile = testProfiles[0]
        mockMvc
            .perform(get("/API/profiles/${expectedProfile.id}").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.profileId").exists(),
                jsonPath("$.email").value(expectedProfile.email),
                jsonPath("$.name").value(expectedProfile.name),
                jsonPath("$.surname").value(expectedProfile.surname),
                jsonPath("$.phoneNumber").value(expectedProfile.phoneNumber),
                jsonPath("$.address").value(expectedProfile.address),
                jsonPath("$.city").value(expectedProfile.city),
                jsonPath("$.country").value(expectedProfile.country),
            )
    }

    @Test
    @Transactional
    @Rollback
    fun getProfileWithTicketsClient() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        profileRepository.save(testProfiles[0])
        val expectedProfile = testProfiles[0]
        mockMvc
            .perform(get("/API/profiles/${expectedProfile.id}").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.profileId").exists(),
                jsonPath("$.email").value(expectedProfile.email),
                jsonPath("$.name").value(expectedProfile.name),
                jsonPath("$.surname").value(expectedProfile.surname),
                jsonPath("$.phoneNumber").value(expectedProfile.phoneNumber),
                jsonPath("$.address").value(expectedProfile.address),
                jsonPath("$.city").value(expectedProfile.city),
                jsonPath("$.country").value(expectedProfile.country),
            )
    }

    @Test
    @Transactional
    @Rollback
    fun getProfileWithTicketsExpertUnauthorized() {
        SecurityTestUtils.setExpert(testExperts[0].email)
        profileRepository.save(testProfiles[0])
        val expectedProfile = testProfiles[0]
        mockMvc
            .perform(get("/API/profiles/${expectedProfile.id}").contentType("application/json"))
            .andExpectAll(
                status().isUnauthorized,
                jsonPath("$.error").doesNotExist()
            )
    }

    @Test
    @Transactional
    @Rollback
    fun getProfileWithTicketsManager() {
        profileRepository.save(testProfiles[0])
        val expectedProfile = testProfiles[0]
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/profiles/${expectedProfile.id}").contentType("application/json"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.profileId").exists(),
                jsonPath("$.email").value(expectedProfile.email),
                jsonPath("$.name").value(expectedProfile.name),
                jsonPath("$.surname").value(expectedProfile.surname),
                jsonPath("$.phoneNumber").value(expectedProfile.phoneNumber),
                jsonPath("$.address").value(expectedProfile.address),
                jsonPath("$.city").value(expectedProfile.city),
                jsonPath("$.country").value(expectedProfile.country),
            )
    }

    @Test
    fun getProfileNotFound() {
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/profiles/${Int.MAX_VALUE}").contentType("application/json"))
            .andExpectAll(
                status().isNotFound,
                jsonPath("$.error").doesNotExist()
            )
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["plainaddress",
            "Joe Smith <email@example.com>",
            "email.example.com",
            "i .@..it",
            "email@example_com",
            "email@example!com",
            "email@example#com",
            "email@example\$com",
            "email@example&com",
            "email@example'com",
            "email@example*com",
            "email@example+com",
            "email@example=com",
            "email@example|com"]
    )
    fun getProfileInvalidEmail(invalidEmail: String) {
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/profiles/$invalidEmail").contentType("application/json"))
            .andExpectAll(
                status().isUnprocessableEntity,
                jsonPath("$.error").exists()
            )
    }

    /////////////////////////////////////////////////////////////////////
    ////// PUT /API/profiles/{email}
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun modifyProfileClient() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = EditProfileDTO(
            name = "NewName",
            surname = oldProfileDTO.surname,
            phoneNumber = oldProfileDTO.phoneNumber,
            address = oldProfileDTO.address,
            city = oldProfileDTO.city,
            country = oldProfileDTO.country
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
    }

    @Test
    @Transactional
    @Rollback
    fun modifyProfileExpert() {
        SecurityTestUtils.setExpert(testExperts[0].email)
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = EditProfileDTO(
            name = "NewName",
            surname = oldProfileDTO.surname,
            phoneNumber = oldProfileDTO.phoneNumber,
            address = oldProfileDTO.address,
            city = oldProfileDTO.city,
            country = oldProfileDTO.country
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @Transactional
    @Rollback
    fun modifyProfileManager() {
        SecurityTestUtils.setManager()
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = EditProfileDTO(
            name = "NewName",
            surname = oldProfileDTO.surname,
            phoneNumber = oldProfileDTO.phoneNumber,
            address = oldProfileDTO.address,
            city = oldProfileDTO.city,
            country = oldProfileDTO.country
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @Transactional
    @Rollback
    fun modifyProfilePhoneNumber() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val oldProfileDTO = testProfiles[0].toDTO()
        val newProfileDTO = EditProfileDTO(
            name = oldProfileDTO.name,
            surname = oldProfileDTO.surname,
            phoneNumber = "4444444444",
            address = oldProfileDTO.address,
            city = oldProfileDTO.city,
            country = oldProfileDTO.country
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
    }

    @Test
    @Transactional
    @Rollback
    fun modifyProfileComplete() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val newProfileDTO = ProfileTestUtils.getNewEditProfileDTO()

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
    }

    @Test
    fun modifyProfileDuplicatePhoneNumber() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val oldProfileDTO = testProfiles[0].toDTO()
        val otherProfileDTO = testProfiles[1].toDTO()
        val newProfileDTO = EditProfileDTO(
            name = oldProfileDTO.name,
            surname = oldProfileDTO.surname,
            phoneNumber = otherProfileDTO.phoneNumber,
            address = oldProfileDTO.address,
            city = oldProfileDTO.city,
            country = oldProfileDTO.country
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isConflict)
    }

    @Test
    fun modifyProfileWrongPhoneNumber() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val newProfileDTO = ProfileTestUtils.getNewEditProfileDTO().copy(
            phoneNumber = "abc"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.error").exists() }
    }

    @Test
    fun modifyProfileWrongName() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val newProfileDTO = ProfileTestUtils.getNewEditProfileDTO().copy(
            name = "@!'ì23190!--@#è]"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.error").exists() }
    }

    @Test
    fun modifyProfileWrongSurname() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val newProfileDTO = ProfileTestUtils.getNewEditProfileDTO().copy(
            surname = "Do Silva £$%&!()/{}"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.error").exists() }
    }

    @Test
    fun modifyProfileEmptyBody() {
        SecurityTestUtils.setClient(testProfiles[0].email)

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString("")

        mockMvc
            .perform(
                put("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.error").exists() }
    }

    @Test
    fun modifyProfileEmptyFields() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val newProfileDTO = ProfileTestUtils.getNewEditProfileDTO().copy(
            name = "",
            surname = "",
            address = "",
            city = "",
            country = ""
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.error").exists() }
    }
}
