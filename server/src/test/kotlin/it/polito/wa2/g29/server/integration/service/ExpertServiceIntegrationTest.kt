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
import it.polito.wa2.g29.server.utils.TestExpertUtils
import it.polito.wa2.g29.server.utils.TestProductUtils
import it.polito.wa2.g29.server.utils.TestProfileUtils
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpertServiceIntegrationTest : AbstractTestcontainersTest() {
    @Autowired
    private lateinit var expertService: ExpertService

    @Autowired
    private lateinit var ticketStatusChangeService: TicketStatusChangeService

    @Autowired
    private lateinit var expertRepository: ExpertRepository

    @Autowired
    private lateinit var skillRepository: SkillRepository

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
        productRepository.deleteAllInBatch()
        profileRepository.deleteAllInBatch()
        testProducts = TestProductUtils.insertProducts(productRepository)
        testProfiles = TestProfileUtils.insertProfiles(profileRepository)
    }

    @BeforeEach
    fun setup() {
        ticketRepository.deleteAllInBatch()
        skillRepository.deleteAllInBatch()
        expertRepository.deleteAllInBatch()
        testExperts = TestExpertUtils.insertExperts(expertRepository)
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
        val expectedExperts = testExperts

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
        val expectedExpertDTO = testExperts[0].toDTO()

        val actualExpertDTO = expertService.getExpertById(expectedExpertDTO.expertId!!)

        assert(actualExpertDTO == expectedExpertDTO)
    }

    @Test
    fun getExpertByIdNotFound() {
        assertThrows<ExpertNotFoundException> {
            expertService.getExpertById(99999999)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// getAllTicketsByExpertId
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
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
        TestExpertUtils.addTicket(ticketRepository, expertOne, ticketForExpertOne)
        TestExpertUtils.addTicket(ticketRepository, expertTwo, ticketForExpertTwo)

        val expertOneActualTickets = expertService.getAllTicketsByExpertId(expertOne.id!!)
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
            TestExpertUtils.addTicket(ticketRepository, expert, it)
        }

        val actualExpertTicketsDTO = expertService.getAllTicketsByExpertId(expert.id!!)

        assert(expectedExpertTickets.size == actualExpertTicketsDTO.size)
        val expectedExpertTicketsDTOSorted = expectedExpertTickets.sortedWith(compareBy<Ticket> { it.status }
            .thenByDescending { it.priorityLevel }
            .thenByDescending { it.lastModifiedAt })
            .map { it.toDTO() }
        assert(expectedExpertTicketsDTOSorted == actualExpertTicketsDTO)
    }

    @Test
    @Transactional
    fun getAllTicketsByExpertIdWithoutTickets() {
        val expert = testExperts[0]

        val actualExpertTicketsDTO = expertService.getAllTicketsByExpertId(expert.id!!)

        assert(actualExpertTicketsDTO.isEmpty())
    }

    @Test
    fun getAllTicketsByExpertIdNotFound() {
        val nonExistingExpertId = Int.MAX_VALUE

        assertThrows<ExpertNotFoundException> {
            expertService.getAllTicketsByExpertId(nonExistingExpertId)
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// getTicketStatusChangesByExpertId
    /////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
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
        TestExpertUtils.addTicket(ticketRepository, expertOne, ticketForExpertOne)
        TestExpertUtils.addTicket(ticketRepository, expertTwo, ticketForExpertTwo)
        TestExpertUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expertOne,
            ticketForExpertOne,
            TicketStatus.RESOLVED,
            UserType.EXPERT,
            "The issue has been resolved"
        )
        TestExpertUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expertTwo,
            ticketForExpertTwo,
            TicketStatus.RESOLVED,
            UserType.EXPERT,
            null
        )

        val expertOneActualTicketsStatusChangesDTO = expertService.getTicketStatusChangesByExpertId(expertOne.id!!)
        val expertTwoActualTicketsStatusChangesDTO = expertService.getTicketStatusChangesByExpertId(expertTwo.id!!)

        assert(expertOneActualTicketsStatusChangesDTO.isNotEmpty())
        assert(expertTwoActualTicketsStatusChangesDTO.isNotEmpty())
        assert(expertOneActualTicketsStatusChangesDTO.size == 1)
        assert(expertTwoActualTicketsStatusChangesDTO.size == 1)
    }

    @Test
    @Transactional
    fun getTicketStatusChangesByExpertIdWithManyTickets() {
        val expert = testExperts[0]
        val ticketOne = Ticket("title1", "description1", testProducts[0], testProfiles[0]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }
        val ticketTwo = Ticket("title2", "description2", testProducts[0], testProfiles[1]).apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = TicketPriority.LOW
        }
        TestExpertUtils.addTicket(ticketRepository, expert, ticketOne)
        TestExpertUtils.addTicket(ticketRepository, expert, ticketTwo)
        TestExpertUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expert,
            ticketOne,
            TicketStatus.RESOLVED,
            UserType.EXPERT,
            ""
        )
        TestExpertUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expert,
            ticketOne,
            TicketStatus.CLOSED,
            UserType.CUSTOMER,
            "It works now"
        )
        TestExpertUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expert,
            ticketTwo,
            TicketStatus.RESOLVED,
            UserType.EXPERT,
            "The issue has been resolved"
        )
        TestExpertUtils.addTicketStatusChange(
            ticketStatusChangeService,
            expert,
            ticketTwo,
            TicketStatus.CLOSED,
            UserType.EXPERT,
            null
        )

        val expertActualTicketsStatusChangesDTO = expertService.getTicketStatusChangesByExpertId(expert.id!!)

        assert(expertActualTicketsStatusChangesDTO.isNotEmpty())
        assert(expertActualTicketsStatusChangesDTO.size == 3)
        assert(expertActualTicketsStatusChangesDTO.map { it.changedBy }.none { it != UserType.EXPERT.toString() })
        for (i in 0 until expertActualTicketsStatusChangesDTO.size - 1) {
            assert(expertActualTicketsStatusChangesDTO[i].time >= expertActualTicketsStatusChangesDTO[i + 1].time)
        }
    }

    @Test
    @Transactional
    fun getTicketStatusChangesByExpertIdWithoutTickets() {
        val expert = testExperts[0]

        val expertActualTicketsStatusChangesDTO = expertService.getTicketStatusChangesByExpertId(expert.id!!)

        assert(expertActualTicketsStatusChangesDTO.isEmpty())
    }

    @Test
    fun getTicketStatusChangesByExpertIdNotFound() {
        val nonExistingExpertId = Int.MAX_VALUE

        assertThrows<ExpertNotFoundException> {
            expertService.getTicketStatusChangesByExpertId(nonExistingExpertId)
        }
    }

}
