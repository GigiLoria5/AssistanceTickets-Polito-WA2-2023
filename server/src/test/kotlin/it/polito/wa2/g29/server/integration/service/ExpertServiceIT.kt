package it.polito.wa2.g29.server.integration.service

import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.exception.ExpertNotFoundException
import it.polito.wa2.g29.server.integration.AbstractTestcontainersTest
import it.polito.wa2.g29.server.model.*
import it.polito.wa2.g29.server.repository.*
import it.polito.wa2.g29.server.service.ExpertService
import it.polito.wa2.g29.server.service.TicketStatusChangeService
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
    private lateinit var ticketStatusChangeService: TicketStatusChangeService

    @Autowired
    private lateinit var expertRepository: ExpertRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var profileRepository: ProfileRepository

    @Autowired
    private lateinit var ticketRepository: TicketRepository

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
    @Rollback
    fun getAllTicketsByExpertIdOnlyExpertTicketsReturned() {
        val expertOne = testExperts[0]
        val expertTwo = testExperts[1]
        val ticketForExpertOne = Ticket("title1", "description1", testProducts[0], testProfiles[0]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }
        val ticketForExpertTwo = Ticket("title2", "description2", testProducts[1], testProfiles[0]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }
        ExpertTestUtils.addTicket(ticketRepository, expertOne, ticketForExpertOne)
        ExpertTestUtils.addTicket(ticketRepository, expertTwo, ticketForExpertTwo)

        SecurityTestUtils.setExpert(expertOne.email)
        val expertOneActualTickets = expertService.getAllTicketsByExpertId(expertOne.id!!)
        SecurityTestUtils.setExpert(expertTwo.email)
        val expertTwoActualTickets = expertService.getAllTicketsByExpertId(expertTwo.id!!)

        assert(expertOneActualTickets.isNotEmpty())
        assert(expertTwoActualTickets.isNotEmpty())
        assert(expertOneActualTickets.contains(ticketForExpertOne.toDTO()))
        assert(!expertOneActualTickets.contains(ticketForExpertTwo.toDTO()))
        assert(expertTwoActualTickets.contains(ticketForExpertTwo.toDTO()))
        assert(!expertTwoActualTickets.contains(ticketForExpertOne.toDTO()))
    }

    @Test
    @Transactional
    @Rollback
    fun getAllTicketsByExpertIdWithManyTickets() {
        val expert = testExperts[0]
        val ticketOne = Ticket("title1", "description1", testProducts[0], testProfiles[0]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.HIGH
        }
        val ticketTwo = Ticket("title2", "description2", testProducts[1], testProfiles[0]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.CRITICAL
        }
        val ticketThree = Ticket("title3", "description3", testProducts[0], testProfiles[1]).apply {
            status = TicketStatus.CLOSED
            priorityLevel = TicketPriority.LOW
        }
        val ticketFour = Ticket("title4", "description4", testProducts[1], testProfiles[1]).apply {
            status = TicketStatus.RESOLVED
            priorityLevel = TicketPriority.MEDIUM
        }
        val ticketFive = Ticket("title5", "description5", testProducts[0], testProfiles[1]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.HIGH
        }
        val expectedExpertTickets = listOf(ticketOne, ticketTwo, ticketThree, ticketFour, ticketFive)
        expectedExpertTickets.forEach {
            ExpertTestUtils.addTicket(ticketRepository, expert, it)
        }

        SecurityTestUtils.setExpert(expert.email)
        val actualExpertTicketsDTO = expertService.getAllTicketsByExpertId(expert.id!!)

        assert(expectedExpertTickets.size == actualExpertTicketsDTO.size)
        val expectedExpertTicketsDTOSorted = expectedExpertTickets
            .sortedWith(compareByDescending { it.priorityLevel }).map { it.toDTO() }
        assert(expectedExpertTicketsDTOSorted == actualExpertTicketsDTO)
    }

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
    @Rollback
    fun getTicketStatusChangesByExpertIdOnlyExpertTicketsReturned() {
        val expertOne = testExperts[0]
        val expertTwo = testExperts[1]
        val ticketForExpertOne = Ticket("title1", "description1", testProducts[0], testProfiles[0]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }
        val ticketForExpertTwo = Ticket("title2", "description2", testProducts[1], testProfiles[0]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }
        ExpertTestUtils.addTicket(ticketRepository, expertOne, ticketForExpertOne)
        ExpertTestUtils.addTicket(ticketRepository, expertTwo, ticketForExpertTwo)
        SecurityTestUtils.setExpert(expertOne.email)
        ExpertTestUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expertOne,
            ticketForExpertOne,
            TicketStatus.RESOLVED,
            UserType.EXPERT,
            "The issue has been resolved"
        )
        SecurityTestUtils.setExpert(expertTwo.email)
        ExpertTestUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expertTwo,
            ticketForExpertTwo,
            TicketStatus.RESOLVED,
            UserType.EXPERT,
            null
        )

        SecurityTestUtils.setExpert(expertOne.email)
        val expertOneActualTicketsStatusChangesDTO = expertService.getTicketStatusChangesByExpertId(expertOne.id!!)
        SecurityTestUtils.setExpert(expertTwo.email)
        val expertTwoActualTicketsStatusChangesDTO = expertService.getTicketStatusChangesByExpertId(expertTwo.id!!)

        assert(expertOneActualTicketsStatusChangesDTO.isNotEmpty())
        assert(expertTwoActualTicketsStatusChangesDTO.isNotEmpty())
        assert(expertOneActualTicketsStatusChangesDTO.size == 1)
        assert(expertTwoActualTicketsStatusChangesDTO.size == 1)
    }

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
