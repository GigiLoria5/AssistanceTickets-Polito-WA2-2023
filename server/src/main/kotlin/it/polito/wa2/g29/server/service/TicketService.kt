package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.*

interface TicketService {
    fun getAllTickets(): List<TicketDTO>
    fun getTicketById(ticketId: Int): TicketDTO
    fun getTicketStatusChangesByTicketId(ticketId: Int): List<TicketChangeDTO>
    fun createTicket(newTicketDTO: NewTicketDTO): TicketIdDTO
    fun startTicket(ticketId: Int,startTicketDTO: StartTicketDTO)
}