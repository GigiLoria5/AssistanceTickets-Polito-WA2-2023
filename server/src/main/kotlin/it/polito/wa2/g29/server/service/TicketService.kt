package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.NewTicketDTO
import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.dto.TicketDTO

interface TicketService {
    fun getAllTickets(): List<TicketDTO>
    fun getTicketById(ticketId: Int): TicketDTO
    fun getTicketStatusChangesByTicketId(ticketId: Int): List<TicketChangeDTO>
    fun createTicket(newTicketDTO: NewTicketDTO)
}