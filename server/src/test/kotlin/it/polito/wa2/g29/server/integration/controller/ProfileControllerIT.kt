package it.polito.wa2.g29.server.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g29.server.dto.profile.EditProfileDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
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
    private lateinit var ticketRepository: TicketRepository

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
    ////// GET /API/profiles/{email}
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getProfileWithoutTickets() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val expectedProfile = testProfiles[0]
        mockMvc
            .perform(get("/API/profiles/${expectedProfile.email}").contentType("application/json"))
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
                jsonPath("$.ticketsIds").isArray,
                jsonPath("$.ticketsIds").isEmpty
            )
    }

    @Test
    @Transactional
    @Rollback
    fun getProfileWithTicketsClient() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        TicketTestUtils.profiles = testProfiles
        TicketTestUtils.products = testProducts
        val tickets = TicketTestUtils.insertTickets(ticketRepository)
        testProfiles[0].tickets.add(tickets.first { it.customer.id == testProfiles[0].id })
        profileRepository.save(testProfiles[0])
        val expectedProfile = testProfiles[0]
        mockMvc
            .perform(get("/API/profiles/${expectedProfile.email}").contentType("application/json"))
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
                jsonPath("$.ticketsIds").isArray,
                jsonPath("$.ticketsIds").isNotEmpty
            )
    }

    @Test
    @Transactional
    @Rollback
    fun getProfileWithTicketsExpert() {
        SecurityTestUtils.setExpert(testExperts[0].email)
        TicketTestUtils.profiles = testProfiles
        TicketTestUtils.products = testProducts
        val tickets = TicketTestUtils.insertTickets(ticketRepository)
        tickets.forEach { TicketTestUtils.startTicket(ticketRepository, it, testExperts[0], TicketPriority.LOW) }
        testProfiles[0].tickets.add(tickets.first { it.customer.id == testProfiles[0].id })
        profileRepository.save(testProfiles[0])
        expertRepository.save(testExperts[0])
        val expectedProfile = testProfiles[0]
        mockMvc
            .perform(get("/API/profiles/${expectedProfile.email}").contentType("application/json"))
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
                jsonPath("$.ticketsIds").isArray,
                jsonPath("$.ticketsIds").isNotEmpty
            )
    }

    @Test
    @Transactional
    @Rollback
    fun getProfileWithTicketsExpertUnauthorized() {
        SecurityTestUtils.setExpert(testExperts[0].email)
        TicketTestUtils.profiles = testProfiles
        TicketTestUtils.products = testProducts
        val tickets = TicketTestUtils.insertTickets(ticketRepository)
        testProfiles[0].tickets.add(tickets.first { it.customer.id == testProfiles[0].id })
        profileRepository.save(testProfiles[0])
        val expectedProfile = testProfiles[0]
        mockMvc
            .perform(get("/API/profiles/${expectedProfile.email}").contentType("application/json"))
            .andExpectAll(
                status().isUnauthorized,
                jsonPath("$.error").doesNotExist()
            )
    }

    @Test
    @Transactional
    @Rollback
    fun getProfileWithTicketsManager() {
        TicketTestUtils.profiles = testProfiles
        TicketTestUtils.products = testProducts
        val tickets = TicketTestUtils.insertTickets(ticketRepository)
        testProfiles[0].tickets.add(tickets.first { it.customer.id == testProfiles[0].id })
        profileRepository.save(testProfiles[0])
        val expectedProfile = testProfiles[0]
        SecurityTestUtils.setManager()
        mockMvc
            .perform(get("/API/profiles/${expectedProfile.email}").contentType("application/json"))
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
                jsonPath("$.ticketsIds").isArray,
                jsonPath("$.ticketsIds").isNotEmpty
            )
    }

    @Test
    fun getProfileNotFound() {
        SecurityTestUtils.setManager()
        val email = "non_existing_email@fake.com"
        mockMvc
            .perform(get("/API/profiles/$email").contentType("application/json"))
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
    ////// POST /API/profiles
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun createProfileManager() {
        SecurityTestUtils.setManager()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO()

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
    @Transactional
    @Rollback
    fun createProfileClient() {
        SecurityTestUtils.setClient(testProfiles[0].email)
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO()

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @Transactional
    @Rollback
    fun createProfileExpert() {
        SecurityTestUtils.setExpert(testExperts[0].email)
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO()

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["btrehearne9@hatena.ne.jp",
            "cspillmanf@list-manage.com",
            "mmanlowo@google.co.uk",
            "yreddyhoff2e@123-reg.co.uk"]
    )
    @Transactional
    @Rollback
    fun createProfileValidEmails(email: String) {
        SecurityTestUtils.setManager()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO().copy(
            email = email,
            phoneNumber = ProfileTestUtils.generateRandomPhoneNumber()
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

    @ParameterizedTest
    @ValueSource(
        strings = ["X AE A-XII Musk",
            "nicola",
            "nicolo'",
            "John Paul",
            "Mary-Jane",
            "O'Connor",
            "John Jr."]
    )
    @Transactional
    @Rollback
    fun createProfileValidNames(name: String) {
        SecurityTestUtils.setManager()
        val timestamp = System.currentTimeMillis()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO().copy(
            email = "test$timestamp@example.com",
            phoneNumber = ProfileTestUtils.generateRandomPhoneNumber(),
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

    @ParameterizedTest
    @ValueSource(
        strings = ["Van der Meer",
            "musk",
            "Kim-Lee",
            "O'Reilly",
            "Santos Jr."]
    )
    @Transactional
    @Rollback
    fun createProfileValidSurname(surname: String) {
        SecurityTestUtils.setManager()
        val timestamp = System.currentTimeMillis()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO().copy(
            email = "test$timestamp@example.com",
            phoneNumber = ProfileTestUtils.generateRandomPhoneNumber(),
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

    @ParameterizedTest
    @ValueSource(
        strings = ["New Address",
            "123 Main St",
            "456 Elm St Apt 5",
            "789 Oak Rd Suite 100",
            "1011 Pine Ave Unit B",
            "1213 Maple Blvd #200",
            "1415 Cedar Ln Building A",
            "1617 Birch Ct Building 2",
            "Corso Duca degli Abruzzi, 24"]
    )
    @Transactional
    @Rollback
    fun createProfileValidAddress(address: String) {
        SecurityTestUtils.setManager()
        val timestamp = System.currentTimeMillis()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO().copy(
            email = "test$timestamp@example.com",
            phoneNumber = ProfileTestUtils.generateRandomPhoneNumber(),
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

    @ParameterizedTest
    @ValueSource(
        strings = ["Bangkok",
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
            "Fuquay-Varina"]
    )
    @Transactional
    @Rollback
    fun createProfileValidCity(city: String) {
        SecurityTestUtils.setManager()
        val timestamp = System.currentTimeMillis()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO().copy(
            email = "test$timestamp@example.com",
            phoneNumber = ProfileTestUtils.generateRandomPhoneNumber(),
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

    @ParameterizedTest
    @ValueSource(
        strings = ["Brazil",
            "Argentina",
            "Thailand",
            "Japan",
            "France",
            "England",
            "Germany",
            "United States",
            "Italy"]
    )
    @Transactional
    @Rollback
    fun createProfileValidCountry(country: String) {
        SecurityTestUtils.setManager()
        val timestamp = System.currentTimeMillis()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO().copy(
            email = "test$timestamp@example.com",
            phoneNumber = ProfileTestUtils.generateRandomPhoneNumber(),
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

    @Test
    fun createProfileDuplicateEmail() {
        SecurityTestUtils.setManager()
        val newProfileDTO = testProfiles[0].toDTO().copy(
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
            .andExpect { jsonPath("$.error").exists() }
    }

    @Test
    fun createProfileDuplicatePhoneNumber() {
        SecurityTestUtils.setManager()
        val newProfileDTO = testProfiles[0].toDTO().copy(
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
            .andExpect { jsonPath("$.error").exists() }
    }

    @Test
    fun createProfileBlankFields() {
        SecurityTestUtils.setManager()
        val newProfileDTO = Profile("", "", "", "", "", "", "").toDTO()

        val mapper = ObjectMapper()
        val jsonBody = mapper.writeValueAsString(newProfileDTO)

        mockMvc
            .perform(
                post("/API/profiles")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity)
            .andExpect { jsonPath("$.error").exists() }
    }

    @Test
    fun createProfileFieldsWithSpace() {
        SecurityTestUtils.setManager()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO().copy(
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
            .andExpect { jsonPath("$.error").exists() }
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["plainaddress",
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
            "   "]
    )
    fun createProfileInvalidEmail(email: String) {
        SecurityTestUtils.setManager()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO().copy(
            email = email,
            phoneNumber = ProfileTestUtils.generateRandomPhoneNumber(),
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
            .andExpect { jsonPath("$.error").exists() }
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["123456789",
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
            "   "]
    )
    fun createProfileInvalidNames(name: String) {
        SecurityTestUtils.setManager()
        val timestamp = System.currentTimeMillis()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO().copy(
            email = "test$timestamp@example.com",
            phoneNumber = ProfileTestUtils.generateRandomPhoneNumber(),
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
            .andExpect { jsonPath("$.error").exists() }
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["123456789",
            "@ndre@",
            "Doe'-.Smith",
            "Do.n-Smi'th",
            "Doe .--n",
            " Smith",
            "Smith ",
            "Smith - ",
            "Smith -",
            "",
            "   "]
    )
    fun createProfileInvalidSurname(surname: String) {
        SecurityTestUtils.setManager()
        val timestamp = System.currentTimeMillis()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO().copy(
            email = "test$timestamp@example.com",
            phoneNumber = ProfileTestUtils.generateRandomPhoneNumber(),
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
            .andExpect { jsonPath("$.error").exists() }
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["test_mail@fake.com", "333--333--3333", "333-3333333", "", "   "]
    )
    fun createProfileInvalidPhoneNumber(phoneNumber: String) {
        SecurityTestUtils.setManager()
        val timestamp = System.currentTimeMillis()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO().copy(
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
            .andExpect { jsonPath("$.error").exists() }
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["@ street",
            "   12312 a  21031 i0 k asadlsa a ",
            "",
            "  "]
    )
    fun createProfileInvalidAddress(address: String) {
        SecurityTestUtils.setManager()
        val timestamp = System.currentTimeMillis()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO().copy(
            email = "test$timestamp@example.com",
            phoneNumber = ProfileTestUtils.generateRandomPhoneNumber(),
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
            .andExpect { jsonPath("$.error").exists() }
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["New York 123",
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
            "   "]
    )
    fun createProfileInvalidCity(city: String) {
        SecurityTestUtils.setManager()
        val timestamp = System.currentTimeMillis()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO().copy(
            email = "test$timestamp@example.com",
            phoneNumber = ProfileTestUtils.generateRandomPhoneNumber(),
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
            .andExpect { jsonPath("$.error").exists() }
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["United States 123",
            "Brazil!",
            "Argentina 123",
            "Thailand 123",
            "Japan 123",
            "France 123",
            "England 123",
            "Germany 123",
            "Italy 123",
            "",
            "   "]
    )
    fun createProfileInvalidCountry(country: String) {
        SecurityTestUtils.setManager()
        val timestamp = System.currentTimeMillis()
        val newProfileDTO = ProfileTestUtils.getNewProfileDTO().copy(
            email = "test$timestamp@example.com",
            phoneNumber = ProfileTestUtils.generateRandomPhoneNumber(),
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
            .andExpect { jsonPath("$.error").exists() }
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
            country = oldProfileDTO.country,
            null
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
            country = oldProfileDTO.country,
            null
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
            country = oldProfileDTO.country,
            null
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
            country = oldProfileDTO.country,
            null
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
            country = oldProfileDTO.country,
            null
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