package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.ExpertNotFoundException
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.*
import it.polito.wa2.g29.server.repository.*
import it.polito.wa2.g29.server.service.ExpertService
import it.polito.wa2.g29.server.utils.ExpertTestUtils
import it.polito.wa2.g29.server.utils.ProductTestUtils
import it.polito.wa2.g29.server.utils.ProfileTestUtils
import it.polito.wa2.g29.server.utils.SecurityTestUtils
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpertServiceIT : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var expertService: ExpertService

    @Autowired
    private lateinit var expertRepository: ExpertRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var profileRepository: ProfileRepository

    lateinit var testProducts: List<Product>
    lateinit var testProfiles: List<Profile>
    lateinit var testExperts: List<Expert>

    @BeforeAll
    fun prepare() {
        testProducts = ProductTestUtils.insertProducts(productRepository)
        testProfiles = ProfileTestUtils.insertProfiles(profileRepository)
        testExperts = ExpertTestUtils.insertExperts(expertRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// getAllExperts
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Rollback
    fun getAllExpertsEmpty() {
        expertRepository.deleteAll()

        val experts = expertService.getAllExperts()

        assert(experts.isEmpty())
    }

    @Test
    fun getAllExperts() {
        val expectedExperts = testExperts

        val actualExperts = expertService.getAllExperts()

        assert(actualExperts.isNotEmpty())
        assert(actualExperts.size == expectedExperts.size)
        expectedExperts.forEach {
            actualExperts.contains(it.toDTO())
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// getExpertById
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getExpertByIdManager() {
        SecurityTestUtils.setManager()
        val expectedExpertDTO = testExperts[0].toDTO()

        val actualExpertDTO = expertService.getExpertById(expectedExpertDTO.expertId!!)

        assert(actualExpertDTO == expectedExpertDTO)
    }

    @Test
    fun getExpertByIdExpert() {
        SecurityTestUtils.setExpert(testExperts[0].email)
        val expectedExpertDTO = testExperts[0].toDTO()

        val actualExpertDTO = expertService.getExpertById(expectedExpertDTO.expertId!!)

        assert(actualExpertDTO == expectedExpertDTO)
    }

    @Test
    fun getExpertByIdOtherExpert() {
        SecurityTestUtils.setExpert(testExperts[1].email)
        val expectedExpertDTO = testExperts[0].toDTO()

        assertThrows<AccessDeniedException> {
            expertService.getExpertById(expectedExpertDTO.expertId!!)
        }
    }

    @Test
    fun getExpertByIdNotFound() {
        SecurityTestUtils.setManager()
        assertThrows<ExpertNotFoundException> {
            expertService.getExpertById(Int.MAX_VALUE)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// getAllTicketsByExpertId
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    fun getAllTicketsByExpertIdWithoutTickets() {
        val expert = testExperts[0]
        SecurityTestUtils.setExpert(expert.email)
        val actualExpertTicketsDTO = expertService.getAllTicketsByExpertId(expert.id!!)

        assert(actualExpertTicketsDTO.isEmpty())
    }

    @Test
    @Transactional
    fun getAllTicketsByExpertIdWithoutTicketsManager() {
        val expert = testExperts[0]
        SecurityTestUtils.setManager()
        val actualExpertTicketsDTO = expertService.getAllTicketsByExpertId(expert.id!!)

        assert(actualExpertTicketsDTO.isEmpty())
    }

    @Test
    @Transactional
    fun getAllTicketsByExpertIdWithoutTicketsClient() {
        val expert = testExperts[0]
        SecurityTestUtils.setExpert(testExperts[1].email)

        assertThrows<AccessDeniedException> {
            expertService.getAllTicketsByExpertId(expert.id!!)
        }
    }

    @Test
    fun getAllTicketsByExpertIdNotFound() {
        val nonExistingExpertId = Int.MAX_VALUE
        SecurityTestUtils.setManager()
        assertThrows<ExpertNotFoundException> {
            expertService.getAllTicketsByExpertId(nonExistingExpertId)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// getTicketStatusChangesByExpertId
    /////////////////////////////////////////////////////////////////////


    @Test
    @Transactional
    fun getTicketStatusChangesByExpertIdWithoutTickets() {
        val expert = testExperts[0]
        SecurityTestUtils.setExpert(expert.email)
        val expertActualTicketsStatusChangesDTO = expertService.getTicketStatusChangesByExpertId(expert.id!!)

        assert(expertActualTicketsStatusChangesDTO.isEmpty())
    }

    @Test
    fun getTicketStatusChangesByExpertIdNotFound() {
        val nonExistingExpertId = Int.MAX_VALUE
        SecurityTestUtils.setManager()
        assertThrows<ExpertNotFoundException> {
            expertService.getTicketStatusChangesByExpertId(nonExistingExpertId)
        }
    }

}
