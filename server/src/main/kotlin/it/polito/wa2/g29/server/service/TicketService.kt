package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.*
import it.polito.wa2.g29.server.dto.ticketDTOs.TicketStatusChangeGenericDTO
import it.polito.wa2.g29.server.dto.ticketDTOs.NewTicketDTO
import it.polito.wa2.g29.server.dto.ticketDTOs.TicketStatusChangeInProgressDTO
import it.polito.wa2.g29.server.dto.ticketDTOs.NewTicketIdDTO
import it.polito.wa2.g29.server.enums.TicketStatus

interface TicketService {
    fun getAllTickets(): List<TicketDTO>
    fun getTicketsByStatus(status: TicketStatus): List<TicketDTO>
    fun getTicketById(ticketId: Int): TicketDTO
    fun getTicketStatusChangesByTicketId(ticketId: Int): List<TicketChangeDTO>
    fun createTicket(newTicketDTO: NewTicketDTO): NewTicketIdDTO
    fun ticketStatusChangeInProgress(ticketId: Int, statusChangeData: TicketStatusChangeInProgressDTO)
    fun ticketStatusChangeGeneric(
        ticketId: Int,
        newStatus: TicketStatus,
        statusChangeData: TicketStatusChangeGenericDTO
    )
}