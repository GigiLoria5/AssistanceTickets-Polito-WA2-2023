package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.dto.TicketDTO
import it.polito.wa2.g29.server.dto.ticketDTOs.TicketStatusChangeDTO
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.model.*
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketStatusChangeService

object TestTicketUtils {

    lateinit var products: List<Product>
    lateinit var profiles: List<Profile>
    lateinit var experts: List<Expert>

    fun insertTickets(ticketRepository: TicketRepository): List<Ticket> {
        val newTickets = getTickets()
        ticketRepository.saveAll(newTickets)
        return newTickets
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
            TicketStatusChangeDTO(userType, description)
        )
        expert.ticketChanges.add(TicketChange(ticket, oldStatus, userType, description))
    }

    private fun getTickets(): List<Ticket> {
        val tickets = listOf(
            Ticket(
                title = "title1",
                description = "description1",
                product = products[0],
                customer = profiles[0]
            ),
            Ticket(
                title = "title1",
                description = "description1",
                product = products[1],
                customer = profiles[1]
            )
        )
        return tickets
    }

    fun getNewTicketDTO(): TicketDTO {
        return TicketDTO(
            ticketId = null,
            title = "newtitle",
            description = "newdescription",
            productId = products[0].id,
            customerId = profiles[1].id,
            expertId = null,
            totalExchangedMessages = 0,
            status = "OPEN",
            priorityLevel = null,
            createdAt = 0,
            lastModifiedAt = 0
        )
    }

    fun startTicket(ticketRepository: TicketRepository, ticket: Ticket, expert: Expert, priority: TicketPriority) {
        ticket.apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = priority
        }
        TestExpertUtils.addTicket(ticketRepository, expert, ticket)
    }
}