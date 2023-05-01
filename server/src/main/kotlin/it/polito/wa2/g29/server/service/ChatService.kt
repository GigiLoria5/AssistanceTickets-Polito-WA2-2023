package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.MessageDTO
import it.polito.wa2.g29.server.dto.NewMessageDTO
import it.polito.wa2.g29.server.dto.NewMessageIdDTO

interface ChatService {
    fun getMessagesByTicketId(ticketId: Int): List<MessageDTO>

    fun addMessageWithAttachments(ticketId: Int, newMessage: NewMessageDTO): NewMessageIdDTO
}