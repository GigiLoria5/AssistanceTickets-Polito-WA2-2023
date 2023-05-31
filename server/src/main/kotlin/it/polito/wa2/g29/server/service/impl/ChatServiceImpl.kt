package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.*
import it.polito.wa2.g29.server.enums.AttachmentType
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.exception.*
import it.polito.wa2.g29.server.model.Attachment
import it.polito.wa2.g29.server.model.Message
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.MessageRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.ChatService
import it.polito.wa2.g29.server.utils.AuthenticationUtil
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class ChatServiceImpl(
    private val messageRepository: MessageRepository,
    private val ticketRepository: TicketRepository,
    private val expertRepository: ExpertRepository,
    private val profileRepository: ProfileRepository
) : ChatService {

    private val log = LoggerFactory.getLogger(ChatServiceImpl::class.java)
    override fun getMessagesByTicketId(ticketId: Int): List<MessageDTO> {
        val ticket = ticketRepository.findByIdOrNull(ticketId)
            ?: run{
                log.info("Ticket not found")
                throw TicketNotFoundException()
            }
        checkUserAuthorisation(ticket)
        return ticket.messages.sortedWith(compareBy { it.time }).map { it.toDTO() }
    }

    override fun addMessageWithAttachments(ticketId: Int, newMessage: NewMessageDTO): NewMessageIdDTO {
        val ticket = ticketRepository.findByIdOrNull(ticketId)
            ?: run {
                log.info("Ticket not found")
                throw TicketNotFoundException()
            }
        checkUserAuthorisation(ticket)
        if (ticket.status !in setOf(TicketStatus.RESOLVED, TicketStatus.IN_PROGRESS))
            throw ChatIsInactiveException("impossible to send the message as the chat is inactive")

        val message = Message(
            sender = AuthenticationUtil.getUserTypeEnum(),
            content = newMessage.content,
            ticket = ticket,
            expert = ticket.expert
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

    override fun getAttachment(ticketId: Int, messageId: Int, attachmentId: Int): FileAttachmentDTO {
        val ticket = ticketRepository.findByIdOrNull(ticketId)
            ?: run {
                log.info("Ticket not found")
                throw TicketNotFoundException()
            }
        checkUserAuthorisation(ticket)
        val message = ticket.messages.find { it.id == messageId }
            ?: run {
                log.info("Message not found")
                throw  MessageNotFoundException()
            }
        val attachment = message.attachments.find { it.id == attachmentId }
            ?: run {
                log.info("Attachment not found")
                throw AttachmentNotFoundException()
            }
        return FileAttachmentDTO(attachment.name, attachment.type, attachment.file)
    }

    private fun checkUserAuthorisation(ticket: Ticket) {
        if (!AuthenticationUtil.authenticatedUserIsAssociatedWithTicket(
                ticket,
                { expertRepository.findExpertByEmail(it)!! },
                { profileRepository.findProfileByEmail(it)!! }
            )
        )
            throw AccessDeniedException("")
    }

}
