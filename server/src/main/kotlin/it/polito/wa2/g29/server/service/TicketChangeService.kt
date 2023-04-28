package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.TicketChangeDTO

interface TicketChangeService {

    fun getTicketStatusChangesByTicketId(ticketId: Int): List<TicketChangeDTO>

}