package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeDTO
import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeInProgressDTO
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.exception.DuplicateTicketException
import it.polito.wa2.g29.server.exception.ExpertNotFoundException
import it.polito.wa2.g29.server.exception.NotValidStatusChangeException
import it.polito.wa2.g29.server.exception.TicketNotFoundException
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketStatusChangeService
import it.polito.wa2.g29.server.utils.AuthenticationUtil
import it.polito.wa2.g29.server.utils.TicketStatusChangeRules
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class TicketStatusChangeServiceImpl(
    private val ticketRepository: TicketRepository,
    private val expertRepository: ExpertRepository,
    private val profileRepository: ProfileRepository
) : TicketStatusChangeService {

    private val log = LoggerFactory.getLogger(TicketStatusChangeServiceImpl::class.java)

    @Transactional
    override fun ticketStatusChangeInProgress(ticketId: Int, statusChangeData: TicketStatusChangeInProgressDTO) {
        val ticket = ticketRepository.findByIdOrNull(ticketId)
            ?: run {
                log.info("Ticket not found")
                throw TicketNotFoundException()
            }
        val expert = expertRepository.findByIdOrNull(statusChangeData.expertId)
            ?: run {
                log.info("Expert not found")
                throw ExpertNotFoundException()
            }
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
        val ticket = ticketRepository.findByIdOrNull(ticketId)
            ?: run {
                log.info("Ticket not found.")
                throw TicketNotFoundException()
            }
        //generic check. Authorization first level check done in controller
        checkUserAuthorisation(ticket)
        updateTicketStatus(ticket, newStatus, AuthenticationUtil.getUserTypeEnum(), statusChangeData.description)
    }

    //In this function I try to create a status change and its log, and thrown an exception if it not possible
    private fun updateTicketStatus(ticket: Ticket, newStatus: TicketStatus, changedBy: UserType, description: String?) {
        if (!TicketStatusChangeRules.isValidStatusChange(ticket.status, newStatus)) {
            val task = TicketStatusChangeRules.getTaskToAchieveStatus(newStatus)
            log.info("Could not {} the ticket: {} because its current status is: {}", task, ticket.id, ticket.status)
            throw NotValidStatusChangeException("Could not $task the ticket with id ${ticket.id} because its current status is '${ticket.status}'")
        }

        if (newStatus == TicketStatus.REOPENED) {
            val notClosedAndNotResolvedTicket = ticketRepository.findTicketByCustomerAndProductTokenAndStatusNotAndStatusNot(
                ticket.customer,
                ticket.productToken,
                TicketStatus.CLOSED,
                TicketStatus.RESOLVED
            )

            //throw an exception if a not closed/resolved ticket for the same customer and productToken already exists
            if (notClosedAndNotResolvedTicket != null)
                throw DuplicateTicketException("A not closed ticket with the same customer and productToken already exists")
        }

        ticket.changeStatus(newStatus, changedBy, description)
        ticketRepository.save(ticket)
    }

    private fun checkUserAuthorisation(ticket: Ticket) {
        when (AuthenticationUtil.getUserTypeEnum()) {
            UserType.EXPERT, UserType.CUSTOMER -> {
                if (!AuthenticationUtil.authenticatedUserIsAssociatedWithTicket(
                        ticket,
                        { expertRepository.findExpertByEmail(it)!! },
                        { profileRepository.findProfileByEmail(it)!! }
                    )
                )
                    throw AccessDeniedException("")
            }

            UserType.MANAGER -> Unit
        }
    }

}
