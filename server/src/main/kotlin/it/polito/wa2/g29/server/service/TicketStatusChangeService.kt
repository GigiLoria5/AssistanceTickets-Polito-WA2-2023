package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeDTO
import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeInProgressDTO
import it.polito.wa2.g29.server.enums.TicketStatus

interface TicketStatusChangeService {
    fun ticketStatusChangeInProgress(ticketId: Int, statusChangeData: TicketStatusChangeInProgressDTO)

    fun ticketStatusChange(ticketId: Int, newStatus: TicketStatus, statusChangeData: TicketStatusChangeDTO)
}
