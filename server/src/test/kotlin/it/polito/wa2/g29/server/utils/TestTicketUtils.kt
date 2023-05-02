package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.TicketRepository

object TestTicketUtils {

    lateinit var products: List<Product>
    lateinit var profiles: List<Profile>

    /**
     * Inserts a list of new tickets into the provided [ticketRepository] and returns an array of the newly added tickets.
     * @param ticketRepository the repository where the tickets should be saved
     * @return an array of the newly added tickets, with a guaranteed size of 2
     */
    fun insertTickets(ticketRepository: TicketRepository): List<Ticket> {
        val newTickets = listOf(
            Ticket(
                title = "Broken airpods",
                description = "My airpods fell and now I can't hear anything",
                product = products[0],
                customer = profiles[0]
            ),
            Ticket(
                title = "Broken screen",
                description = "My computer fell and the screen is shattered",
                product = products[1],
                customer = profiles[1]
            )
        )
        ticketRepository.saveAll(newTickets)
        return newTickets
    }

    fun startTicket(ticketRepository: TicketRepository, ticket: Ticket, expert: Expert, priority: TicketPriority) {
        ticket.apply {
            status = TicketStatus.IN_PROGRESS
            priorityLevel = priority
        }
        TestExpertUtils.addTicket(ticketRepository, expert, ticket)
    }

}