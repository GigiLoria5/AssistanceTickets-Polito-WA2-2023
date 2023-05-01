package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.TicketRepository

object TestTicketUtils {

    lateinit var products: List<Product>
    lateinit var profiles: List<Profile>

    private val tickets = listOf(
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

    fun insertTickets(ticketRepository: TicketRepository): List<Ticket> {
        val newTickets = tickets.map { Ticket(it.title, it.description, it.product, it.customer) }
        ticketRepository.saveAll(newTickets)
        return newTickets
    }
}