package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.*
import it.polito.wa2.g29.server.enums.AttachmentType
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.exception.*
import it.polito.wa2.g29.server.model.Attachment
import it.polito.wa2.g29.server.model.Message
import it.polito.wa2.g29.server.repository.MessageRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.ChatService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ChatServiceImpl(
    private val messageRepository: MessageRepository,
    private val ticketRepository: TicketRepository,
) : ChatService {
    override fun getMessagesByTicketId(ticketId: Int): List<MessageDTO> {
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        return ticket.messages.sortedWith(compareBy { it.time }).map { it.toDTO() }
    }

    override fun addMessageWithAttachments(ticketId: Int, newMessage: NewMessageDTO): NewMessageIdDTO {
        if (newMessage.sender == UserType.MANAGER)
            throw UserTypeNotValidException()
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        if (ticket.status !in setOf(TicketStatus.RESOLVED, TicketStatus.IN_PROGRESS))
            throw ChatIsInactiveException("impossible to send the message as the chat is inactive")
        else {
            val message = Message(
                sender = newMessage.sender,
                content = newMessage.content,
                ticket = ticket,
                expert = if (newMessage.sender === UserType.EXPERT) ticket.expert else null
            )
            message.attachments = newMessage.attachments?.map { attachment ->
                Attachment(
                    name = attachment.originalFilename ?: "no_name",
                    file = attachment.bytes,
                    type = AttachmentType.fromMimeType(attachment.contentType ?: ""),
                    message = message
                )
            }?.toSet() ?: emptySet()

            messageRepository.save(message)
            return NewMessageIdDTO(message.id!!)
        }
    }

    override fun getAttachments(ticketId: Int, messageId: Int, attachmentId: Int): FileAttachmentDTO {
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        val message = ticket.messages.find { it.id == messageId } ?: throw MessageNotFoundException()
        val attachment = message.attachments.find { it.id == attachmentId } ?: throw AttachmentNotFoundException()
        return FileAttachmentDTO(attachment.name, attachment.type, attachment.file)
    }
}