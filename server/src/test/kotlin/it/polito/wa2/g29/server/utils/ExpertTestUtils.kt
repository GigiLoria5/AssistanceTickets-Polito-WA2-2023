package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeDTO
import it.polito.wa2.g29.server.enums.Expertise
import it.polito.wa2.g29.server.enums.Level
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Skill
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.model.TicketChange
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketStatusChangeService

object ExpertTestUtils {

    /**
     * Inserts a list of new experts into the provided [expertRepository] and returns an array of the newly added experts.
     * @param expertRepository the repository where the experts should be saved
     * @return an array of the newly added experts, with a guaranteed size of 3
     */
    fun insertExperts(expertRepository: ExpertRepository): List<Expert> {
        val newExperts = getExperts()
        expertRepository.saveAll(newExperts)
        return newExperts
    }

    fun addTicket(ticketRepository: TicketRepository, expert: Expert, ticket: Ticket) {
        expert.addTicket(ticket)
        ticketRepository.save(ticket)
    }

    fun addTicketStatusChange(
        ticketStatusChangeService: TicketStatusChangeService,
        expert: Expert,
        ticket: Ticket,
        newStatus: TicketStatus,
        userType: UserType,
        description: String?
    ) {
        val oldStatus = ticket.status
        ticketStatusChangeService.ticketStatusChange(
            ticket.id!!,
            newStatus,
            TicketStatusChangeDTO(description)
        )
        expert.ticketChanges.add(TicketChange(ticket, oldStatus, userType, description))
    }

    private fun getExperts(): List<Expert> {
        val experts = listOf(
            Expert(
                name = "John",
                surname = "Doe",
                email = "john.doe@example.com",
                country = "United States",
                city = "New York",
                skills = mutableSetOf()
            ),
            Expert(
                name = "Jane",
                surname = "Doe",
                email = "jane.doe@example.com",
                country = "United States",
                city = "Los Angeles",
                skills = mutableSetOf()
            ),
            Expert(
                name = "Bob",
                surname = "Smith",
                email = "bob.smith@example.com",
                country = "Canada",
                city = "Toronto",
                skills = mutableSetOf()
            )
        )

        val expert0 = experts[0]
        expert0.skills.add(Skill(expertise = Expertise.SMARTPHONE, level = Level.AVERAGE, expert = expert0))
        expert0.skills.add(Skill(expertise = Expertise.COMPUTER, level = Level.SKILLED, expert = expert0))

        val expert1 = experts[1]
        expert1.skills.add(Skill(expertise = Expertise.APPLIANCES, level = Level.EXPERT, expert = expert1))
        expert1.skills.add(
            Skill(
                expertise = Expertise.CONSUMER_ELECTRONICS,
                level = Level.SKILLED,
                expert = expert1
            )
        )
        expert1.skills.add(Skill(expertise = Expertise.SMARTPHONE, level = Level.SPECIALIST, expert = expert1))

        val expert2 = experts[2]
        expert2.skills.add(Skill(expertise = Expertise.COMPUTER, level = Level.BEGINNER, expert = expert2))
        expert2.skills.add(Skill(expertise = Expertise.APPLIANCES, level = Level.AVERAGE, expert = expert2))
        expert2.skills.add(Skill(expertise = Expertise.SMARTPHONE, level = Level.SKILLED, expert = expert2))
        expert2.skills.add(
            Skill(
                expertise = Expertise.CONSUMER_ELECTRONICS,
                level = Level.SPECIALIST,
                expert = expert2
            )
        )
        return experts
    }

}
