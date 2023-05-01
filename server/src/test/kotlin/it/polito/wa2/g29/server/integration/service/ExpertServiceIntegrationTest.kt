package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.ExpertNotFoundException
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.service.ExpertService
import it.polito.wa2.g29.server.utils.TestExpertUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class ExpertServiceIntegrationTest : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var expertService: ExpertService

    @Autowired
    private lateinit var expertRepository: ExpertRepository

    @BeforeEach
    fun setup() {
        expertRepository.deleteAll()
        TestExpertUtils.insertExperts(expertRepository)
    }

    /////////////////////////////////////////////////////////////////////
    ////// getAllExperts
    /////////////////////////////////////////////////////////////////////

    @Test
    fun getAllExpertsEmpty() {
        expertRepository.deleteAll()

        val experts = expertService.getAllExperts()

        assert(experts.isEmpty())
    }

    @Test
    fun getAllExperts() {
        val expectedExperts = TestExpertUtils.experts

        val actualExperts = expertService.getAllExperts()

        println(actualExperts)

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
    fun getExpertById() {
        val expectedExpertDTO = expertRepository.findAll()[0].toDTO()

        val actualExpertDTO = expertService.getExpertById(expectedExpertDTO.expertId!!)

        assert(actualExpertDTO == expectedExpertDTO)
    }

    @Test
    fun getExpertByIdNotFound() {
        assertThrows<ExpertNotFoundException> {
            expertService.getExpertById(99999999)
        }
    }
}