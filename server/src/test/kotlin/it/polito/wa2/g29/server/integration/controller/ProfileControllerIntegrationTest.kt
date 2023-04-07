package it.polito.wa2.g29.server.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.utils.TestProfileUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProfileControllerIntegrationTest : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var profileRepository: ProfileRepository

    @BeforeEach
    fun setup() {
        profileRepository.deleteAll()
        TestProfileUtils.insertProfiles(profileRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// GET /API/profiles/{email}
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getProfile() {
        val expectedProfile = TestProfileUtils.profiles[0]
        mockMvc
            .get("/API/profiles/${expectedProfile.email}")
            .andExpect { status { isOk() } }
            .andExpect { content().contentType(MediaType.APPLICATION_JSON) }
            .andExpectAll {
                jsonPath("$[*].profileId").exists()
                jsonPath("$[*].email").value(expectedProfile.email)
                jsonPath("$[*].name").value(expectedProfile.name)
                jsonPath("$[*].surname").value(expectedProfile.surname)
                jsonPath("$[*].phoneNumber").value(expectedProfile.phoneNumber)
                jsonPath("$[*].address").value(expectedProfile.address)
                jsonPath("$[*].city").value(expectedProfile.city)
                jsonPath("$[*].country").value(expectedProfile.country)
            }
    }

    @Test
    fun getProfileNotFound() {
        val email = "non_existing_email@fake.com"
        mockMvc
            .get("/API/profiles/$email")
            .andExpect { status { isNotFound() } }
            .andExpect { jsonPath("$.errorMessage").doesNotExist() }
    }

    @Test
    fun getProfileEmailNotValidPlain() {
        val email = "plainaddress"
        mockMvc
            .get("/API/profiles/$email")
            .andExpect { status { isUnprocessableEntity() } }
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun getProfileEmailNotValidSpaces() {
        val email = "Joe Smith <email@example.com>"
        mockMvc
            .get("/API/profiles/$email")
            .andExpect { status { isUnprocessableEntity() } }
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun getProfileEmailNotValidMissingAt() {
        val email = "email.example.com"
        mockMvc
            .get("/API/profiles/$email")
            .andExpect { status { isUnprocessableEntity() } }
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun getProfileEmailNotValidStrangeEmail() {
        val email = "this\\ is\"really\"not\\allowed@example.com"
        mockMvc
            .get("/API/profiles/$email")
            .andExpect { status { isUnprocessableEntity() } }
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun getProfileEmailNotValidStrangeEmailSpecialChar() {
        val email = "”(),:;<>[\\]@example.com"
        mockMvc
            .get("/API/profiles/$email")
            .andExpect { status { isUnprocessableEntity() } }
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun getProfileEmailNotValidManyAt() {
        val email = "@%^%#\$@#\$@#.com"
        mockMvc
            .get("/API/profiles/$email")
            .andExpect { status { isUnprocessableEntity() } }
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    /////////////////////////////////////////////////////////////////////
    ////// POST /API/profiles
    /////////////////////////////////////////////////////////////////////

    @Test
    fun createProfile() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy()

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated)
    }

    @Test
    fun createProfileValidNameWithSpaces() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "John Paul"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated)
    }

    @Test
    fun createProfileValidNameWithHyphen() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "Mary-Jane"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated)
    }

    @Test
    fun createProfileValidNameWithApostrophe() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "O'Connor"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated)
    }

    @Test
    fun createProfileValidNameWithPeriod() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "John Jr."
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated)
    }

    @Test
    fun createProfileValidSurnameWithSpaces() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = "Van der Meer"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated)
    }

    @Test
    fun createProfileValidSurnameWithHyphen() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = "Kim-Lee"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated)
    }

    @Test
    fun createProfileValidSurnameWithApostrophe() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = "O'Reilly"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated)
    }

    @Test
    fun createProfileValidSurnameWithPeriod() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = "Santos Jr."
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated)
    }

    @Test
    fun createProfileDuplicateEmail() {
        val newProfileDTO = TestProfileUtils.profiles[0].toDTO().copy(
            profileId = null,
            phoneNumber = "9999999999"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isConflict)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileDuplicatePhoneNumber() {
        val newProfileDTO = TestProfileUtils.profiles[0].toDTO().copy(
            profileId = null,
            email = "new_profile@test.com",
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isConflict)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileBlankFields() {
        val newProfileDTO = Profile().toDTO()

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileFieldsWithSpace() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "   ",
            surname = "   ",
            address = "   ",
            city = "   ",
            country = "   "
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidEmail() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            email = "plainaddress"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidNameType() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "123456789"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidNameSpecialChar() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "#John"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidNameWithConsecutiveSpecialCh() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "John'-.Paul"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidNameWithInvalidCharacterCom() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "Joh.n-Pau'l"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidNameWithInvalidCharacterSequ() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "Joh .--n"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidNameStartingWithSpace() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = " John"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }

    }

    @Test
    fun createProfileInvalidNameEndingWithSpace() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "John "
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }

    }

    @Test
    fun createProfileInvalidNameEndingWithSpaceAndSpecialCharacters() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "John - "
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }

    }

    @Test
    fun createProfileInvalidNameEndingWithSpecialCharacterWithoutFollowingLetters() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "John -"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }

    }

    @Test
    fun createProfileInvalidNameWrongField() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "test_mail@fake.com"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidSurnameType() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = "123456789"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidSurnameSpecialChar() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = "@ndre@"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidSurnameWithConsecutiveSpecialChar() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = "Doe'-.Smith"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidSurnameWithInvalidCharacterCombination() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = "Do.n-Smi'th"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidSurnameWithInvalidCharacterSequence() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = "Doe .--n"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidSurnameStartingWithSpace() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = " Smith"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }

    }

    @Test
    fun createProfileInvalidSurnameEndingWithSpace() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = "Smith "
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }

    }

    @Test
    fun createProfileInvalidSurnameEndingWithSpaceAndSpecialCharacters() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = "Smith - "
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }

    }

    @Test
    fun createProfileInvalidSurnameEndingWithSpecialCharacterWithoutFollowingLetters() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = "Smith -"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }

    }

    @Test
    fun createProfileInvalidSurnameWrongField() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = "test_mail@fake.com"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidPhoneNumber() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            phoneNumber = "test_mail@fake.com"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidPhoneNumberFormat() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            phoneNumber = "333-3333333"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun createProfileInvalidPhoneNumberTooManyDash() {
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            phoneNumber = "333--333--3333"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    /////////////////////////////////////////////////////////////////////
    ////// PUT /API/profiles/{email}
    /////////////////////////////////////////////////////////////////////

    @Test
    fun modifyProfile() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy(
            profileId = null,
            name = "NewName"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles/${oldProfileDTO.email}")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
    }

    @Test
    fun modifyProfileEmail() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy(
            profileId = null,
            email = "new_email@test.org"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles/${oldProfileDTO.email}")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
    }

    @Test
    fun modifyProfilePhoneNumber() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy(
            profileId = null,
            phoneNumber = "4444444444"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles/${oldProfileDTO.email}")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
    }

    @Test
    fun modifyProfileComplete() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = TestProfileUtils.newProfileDTO

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles/${oldProfileDTO.email}")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
    }

    @Test
    fun modifyProfileNotFound() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = oldProfileDTO.copy(
            profileId = null,
            name = "NewName"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles/non_existing_email@fake.it")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound)
    }

    @Test
    fun modifyProfileDuplicateEmail() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val otherProfileDTO = TestProfileUtils.profiles[1].toDTO()
        val newProfileDTO = oldProfileDTO.copy(
            profileId = null,
            email = otherProfileDTO.email
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles/${oldProfileDTO.email}")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isConflict)
    }

    @Test
    fun modifyProfileDuplicatePhoneNumber() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val otherProfileDTO = TestProfileUtils.profiles[1].toDTO()
        val newProfileDTO = oldProfileDTO.copy(
            profileId = null,
            phoneNumber = otherProfileDTO.phoneNumber
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles/${oldProfileDTO.email}")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isConflict)
    }

    @Test
    fun modifyProfileWrongEmailParameter() {
        val newProfileDTO = TestProfileUtils.newProfileDTO

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles/101")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun modifyProfileWrongEmailBody() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            email = "aaaa@12132lc.com@org@.cak12"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles/${oldProfileDTO.email}")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun modifyProfileWrongPhoneNumber() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            phoneNumber = "abc"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles/${oldProfileDTO.email}")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun modifyProfileWrongName() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            name = "@!'ì23190!--@#è]"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles/${oldProfileDTO.email}")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun modifyProfileWrongSurname() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
            surname = "Do Silva £$%&!()/{}"
        )

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                put("/API/profiles/${oldProfileDTO.email}")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun modifyProfileEmptyBody() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString("")

        mockMvc
            .perform(
                put("/API/profiles/${oldProfileDTO.email}")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }

    @Test
    fun modifyProfileEmptyFields() {
        val oldProfileDTO = TestProfileUtils.profiles[0].toDTO()
        val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
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
                put("/API/profiles/${oldProfileDTO.email}")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.errorMessage").exists() }
    }
}