package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.MessageDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.TicketNotFoundException
import it.polito.wa2.g29.server.model.Message
import it.polito.wa2.g29.server.repository.MessageRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.MessageService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val ticketRepository: TicketRepository
) : MessageService {
    override fun getMessagesByTicketId(ticketId: Int): List<MessageDTO> {
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        return ticket.messages.sortedWith(compareBy { it.time }).map { it.toDTO() }
    }
}