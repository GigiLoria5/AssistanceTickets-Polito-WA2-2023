package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.ExpertDTO
import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.dto.TicketDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.ExpertNotFoundException
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.model.TicketChange
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.service.ExpertService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ExpertServiceImpl(
    private val expertRepository: ExpertRepository,
) : ExpertService {
    override fun getAllExperts(): List<ExpertDTO> {
        return expertRepository.findAll().map { it.toDTO() }
    }

    override fun getExpertById(expertId: Int): ExpertDTO {
        val expert = expertRepository.findByIdOrNull(expertId) ?: throw ExpertNotFoundException()
        return expert.toDTO()
    }

    override fun getAllTicketsByExpertId(expertId: Int): List<TicketDTO> {
        val expert = expertRepository.findByIdOrNull(expertId) ?: throw ExpertNotFoundException()
        return expert.tickets.sortedWith(
            compareBy<Ticket> { it.status }.thenByDescending { it.priorityLevel }.thenByDescending { it.lastModifiedAt }
        ).map { it.toDTO() }
    }

    override fun getTicketStatusChangesByExpertId(expertId: Int): List<TicketChangeDTO> {
        val expert = expertRepository.findByIdOrNull(expertId) ?: throw ExpertNotFoundException()
        return expert.ticketChanges.sortedWith(
            compareByDescending<TicketChange> { it.time })
            .map { it.toDTO() }    }
}
