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
    fun getProfileInvalidEmail() {
        val invalidEmails = listOf(
            "plainaddress",
            "Joe Smith <email@example.com>",
            "email.example.com",
            "this\\ is\"really\"not\\allowed@example.com",
            "”(),:;<>[\\]@example.com",
            "@%^%#\$@#\$@#.com",
            "i .@..it",
            "email@example_com",
            "email@example!com",
            "email@example#com",
            "email@example\$com",
            "email@example%com",
            "email@example&com",
            "email@example'com",
            "email@example*com",
            "email@example+com",
            "email@example=com",
            "email@example|com"
        )

        for (email in invalidEmails) {
            mockMvc
                .get("/API/profiles/$email")
                .andExpect { status { isUnprocessableEntity() } }
                .andExpect { jsonPath("$.errorMessage").exists() }
        }
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
    fun createProfileValidEmails() {
        val validEmails =
            listOf(
                "btrehearne9@hatena.ne.jp",
                "cspillmanf@list-manage.com",
                "mmanlowo@google.co.uk",
                "yreddyhoff2e@123-reg.co.uk"
            )

        for (email in validEmails) {
            val timestamp = System.currentTimeMillis()
            val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
                email = email,
                phoneNumber = "5${timestamp % 1000000000}"
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
    }

    @Test
    fun createProfileValidNames() {
        val validNames =
            listOf("X AE A-XII Musk", "nicola", "nicolo'", "John Paul", "Mary-Jane", "O'Connor", "John Jr.")

        for (name in validNames) {
            val timestamp = System.currentTimeMillis()
            val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
                email = "test$timestamp@example.com",
                phoneNumber = "5${timestamp % 1000000000}",
                name = name
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
    }

    @Test
    fun createProfileValidSurname() {
        val validSurnames = listOf("Van der Meer", "musk", "Kim-Lee", "O'Reilly", "Santos Jr.")

        for (surname in validSurnames) {
            val timestamp = System.currentTimeMillis()
            val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
                email = "test$timestamp@example.com",
                phoneNumber = "5${timestamp % 1000000000}",
                surname = surname
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
    }

    @Test
    fun createProfileValidAddress() {
        val validAddresses = listOf(
            "New Address",
            "123 Main St",
            "456 Elm St Apt 5",
            "789 Oak Rd Suite 100",
            "1011 Pine Ave Unit B",
            "1213 Maple Blvd #200",
            "1415 Cedar Ln Building A",
            "1617 Birch Ct Building 2",
            "Corso Duca degli Abruzzi, 24"
        )

        for (address in validAddresses) {
            val timestamp = System.currentTimeMillis()
            val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
                email = "test$timestamp@example.com",
                phoneNumber = "5${timestamp % 1000000000}",
                address = address
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
    }

    @Test
    fun createProfileValidCity() {
        val validCities = listOf(
            "Bangkok",
            "Tokyo",
            "Paris",
            "London",
            "Berlin",
            "Rome",
            "New York",
            "San Francisco",
            "Los Angeles",
            "Buenos Aires",
            "Rio de Janeiro",
            "Fuquay-Varina"
        )

        for (city in validCities) {
            val timestamp = System.currentTimeMillis()
            val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
                email = "test$timestamp@example.com",
                phoneNumber = "5${timestamp % 1000000000}",
                city = city
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
    }

    @Test
    fun createProfileValidCountry() {
        val validCountries = listOf(
            "Brazil",
            "Argentina",
            "Thailand",
            "Japan",
            "France",
            "England",
            "Germany",
            "United States",
            "Italy"
        )

        for (country in validCountries) {
            val timestamp = System.currentTimeMillis()
            val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
                email = "test$timestamp@example.com",
                phoneNumber = "5${timestamp % 1000000000}",
                country = country
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
        val invalidEmails = listOf(
            "plainaddress",
            "Joe Smith <email@example.com>",
            "email.example.com",
            "this\\ is\"really\"not\\allowed@example.com",
            "”(),:;<>[\\]@example.com",
            "@%^%#\$@#\$@#.com",
            "i .@..it",
            "email@example..com",
            "email@example\\com",
            "email@example_com",
            "email@example!com",
            "email@example#com",
            "email@example\$com",
            "email@example%com",
            "email@example&com",
            "email@example'com",
            "email@example*com",
            "email@example+com",
            "email@example=com",
            "email@example|com",
            "",
            "   "
        )

        for (email in invalidEmails) {
            val timestamp = System.currentTimeMillis()
            val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
                email = email,
                phoneNumber = "5${timestamp % 1000000000}",
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
    }

    @Test
    fun createProfileInvalidNames() {
        val invalidNames = listOf(
            "123456789",
            "#John",
            "John'-.Paul",
            "Joh.n-Pau'l",
            "Joh .--n",
            " John",
            "John ",
            "John - ",
            "John -",
            "test_mail@fake.com",
            "",
            "   "
        )

        for (name in invalidNames) {
            val timestamp = System.currentTimeMillis()
            val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
                email = "test$timestamp@example.com",
                phoneNumber = "5${timestamp % 1000000000}",
                name = name
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
    }

    @Test
    fun createProfileInvalidSurname() {
        val invalidSurnames = listOf(
            "123456789",
            "@ndre@",
            "Doe'-.Smith",
            "Do.n-Smi'th",
            "Doe .--n",
            " Smith",
            "Smith ",
            "Smith - ",
            "Smith -",
            "",
            "   "
        )

        for (surname in invalidSurnames) {
            val timestamp = System.currentTimeMillis()
            val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
                email = "test$timestamp@example.com",
                phoneNumber = "5${timestamp % 1000000000}",
                surname = surname
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
    }

    @Test
    fun createProfileInvalidPhoneNumber() {
        val invalidPhoneNumbers = listOf("test_mail@fake.com", "333--333--3333", "333-3333333", "", "   ")

        for (phoneNumber in invalidPhoneNumbers) {
            val timestamp = System.currentTimeMillis()
            val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
                email = "test$timestamp@example.com",
                phoneNumber = phoneNumber
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
    }

    @Test
    fun createProfileInvalidAddress() {
        val invalidAddresses = listOf(
            "@ street",
            "   12312 a  21031 i0 k asadlsa a ",
            "",
            "  "
        )

        for (address in invalidAddresses) {
            val timestamp = System.currentTimeMillis()
            val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
                email = "test$timestamp@example.com",
                phoneNumber = "5${timestamp % 1000000000}",
                address = address
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
    }

    @Test
    fun createProfileInvalidCity() {
        val invalidCities = listOf(
            "New York 123",
            "San Francisco!",
            "Los Angeles 123",
            "Buenos Aires 1",
            "Rio de Janeiro 01",
            "Bangkok 123",
            "Tokyo 123",
            "Paris 123",
            "London 123",
            "Berlin 123",
            "Rome 123",
            "",
            "   "
        )

        for (city in invalidCities) {
            val timestamp = System.currentTimeMillis()
            val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
                email = "test$timestamp@example.com",
                phoneNumber = "5${timestamp % 1000000000}",
                city = city
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
    }

    @Test
    fun createProfileInvalidCountry() {
        val invalidCountries = listOf(
            "United States 123",
            "Brazil!",
            "Argentina 123",
            "Thailand 123",
            "Japan 123",
            "France 123",
            "England 123",
            "Germany 123",
            "Italy 123",
            "",
            "   "
        )

        for (country in invalidCountries) {
            val timestamp = System.currentTimeMillis()
            val newProfileDTO = TestProfileUtils.newProfileDTO.copy(
                email = "test$timestamp@example.com",
                phoneNumber = "5${timestamp % 1000000000}",
                country = country
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