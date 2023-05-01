package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.TicketRepository

object TestTicketUtils {

    val products = TestProductUtils.products
    val profiles = TestProfileUtils.profiles

    val tickets = listOf(
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

    fun insertTickets(ticketRepository: TicketRepository) {
        ticketRepository.saveAll(tickets)
    }
}