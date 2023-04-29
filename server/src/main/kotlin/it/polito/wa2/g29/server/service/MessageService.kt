package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.MessageDTO

interface MessageService {
    fun getMessagesByTicketId(ticketId: Int): List<MessageDTO>
}