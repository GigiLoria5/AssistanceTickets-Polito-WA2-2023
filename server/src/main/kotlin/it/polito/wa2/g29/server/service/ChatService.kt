package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.*

interface ChatService {
    fun getMessagesByTicketId(ticketId: Int): List<MessageDTO>

    fun addMessageWithAttachments(ticketId: Int, newMessage: NewMessageDTO): NewMessageIdDTO

    fun getAttachments(ticketId: Int, messageId: Int, attachmentId: Int): FileAttachmentDTO
}