package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.*
import it.polito.wa2.g29.server.dto.ticket.NewTicketDTO
import it.polito.wa2.g29.server.dto.ticket.NewTicketIdDTO
import it.polito.wa2.g29.server.enums.TicketStatus

interface TicketService {
    fun getAllTickets(): List<TicketDTO>
    fun getTicketsByStatus(status: TicketStatus): List<TicketDTO>
    fun getTicketById(ticketId: Int): TicketDTO
    fun getTicketStatusChangesByTicketId(ticketId: Int): List<TicketChangeDTO>
    fun createTicket(newTicketDTO: NewTicketDTO): NewTicketIdDTO
}