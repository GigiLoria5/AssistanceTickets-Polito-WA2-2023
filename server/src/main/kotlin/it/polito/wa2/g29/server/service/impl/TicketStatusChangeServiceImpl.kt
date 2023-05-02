package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.ticketDTOs.TicketStatusChangeDTO
import it.polito.wa2.g29.server.dto.ticketDTOs.TicketStatusChangeInProgressDTO
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.exception.ExpertNotFoundException
import it.polito.wa2.g29.server.exception.TicketNotFoundException
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketStatusChangeService
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TicketStatusChangeServiceImpl(
    private val ticketRepository: TicketRepository,
    private val expertRepository: ExpertRepository
) : TicketStatusChangeService {
    @Transactional
    override fun ticketStatusChangeInProgress(ticketId: Int, statusChangeData: TicketStatusChangeInProgressDTO) {
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        val expert = expertRepository.findByIdOrNull(statusChangeData.expertId) ?: throw ExpertNotFoundException()
        ticket.expert = expert
        ticket.priorityLevel = statusChangeData.priorityLevel
        updateTicketStatus(ticket, TicketStatus.IN_PROGRESS, UserType.MANAGER, statusChangeData.description)
    }

    @Transactional
    override fun ticketStatusChange(
        ticketId: Int,
        newStatus: TicketStatus,
        statusChangeData: TicketStatusChangeDTO
    ) {
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        updateTicketStatus(ticket, newStatus, statusChangeData.changedBy, statusChangeData.description)
    }

    private fun updateTicketStatus(ticket: Ticket, newStatus: TicketStatus, changedBy: UserType, description: String?) {
        //In this function I try to create a status change and its log, and thrown an exception if it not possible
        ticket.changeStatus(newStatus, changedBy, description)
        ticketRepository.save(ticket)
    }
}