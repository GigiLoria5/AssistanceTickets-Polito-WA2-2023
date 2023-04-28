package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.TicketNotFoundException
import it.polito.wa2.g29.server.model.TicketChange
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketChangeService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TicketChangeServiceImpl(
    private val ticketRepository: TicketRepository
) : TicketChangeService {
    override fun getTicketStatusChangesByTicketId(ticketId: Int): List<TicketChangeDTO> {

        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        return ticket.ticketChanges.sortedWith(
            compareByDescending<TicketChange> { it.time })
            .map { it.toDTO() }
    }
}