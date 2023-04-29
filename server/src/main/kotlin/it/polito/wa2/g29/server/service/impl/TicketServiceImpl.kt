package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.*
import it.polito.wa2.g29.server.dto.ticketDTOs.TicketStatusChangeGenericDTO
import it.polito.wa2.g29.server.dto.ticketDTOs.NewTicketDTO
import it.polito.wa2.g29.server.dto.ticketDTOs.TicketStatusChangeInProgressDTO
import it.polito.wa2.g29.server.dto.ticketDTOs.NewTicketIdDTO
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.exception.*
import it.polito.wa2.g29.server.model.TicketChange
import it.polito.wa2.g29.server.model.toEntity
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketService
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val productRepository: ProductRepository,
    private val profileRepository: ProfileRepository,
    private val expertRepository: ExpertRepository
) : TicketService {
    override fun getAllTickets(): List<TicketDTO> {
        return ticketRepository.findAll().map { it.toDTO() }
    }

    override fun getTicketsByStatus(status: TicketStatus): List<TicketDTO> {
        return ticketRepository.findTicketsByStatus(status).map { it.toDTO() }
    }

    override fun getTicketById(ticketId: Int): TicketDTO {
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        return ticket.toDTO()
    }

    override fun getTicketStatusChangesByTicketId(ticketId: Int): List<TicketChangeDTO> {
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        return ticket.ticketChanges.sortedWith(
            compareByDescending<TicketChange> { it.time })
            .map { it.toDTO() }
    }

    override fun createTicket(newTicketDTO: NewTicketDTO): NewTicketIdDTO {
        //in this function I try to create a new ticket, and I throw exceptions if it is not possible
        val ticket = newTicketDTO.toEntity(productRepository, profileRepository)
        //throw an exception if a not closed ticket for the same customer and product already exists
        if (ticketRepository.findTicketByCustomerAndProductAndStatusNot(
                ticket.customer,
                ticket.product,
                TicketStatus.CLOSED
            ) != null
        )
            throw DuplicateTicketException("A not closed ticket with the same customer and product already exists")
        return NewTicketIdDTO(ticketRepository.save(ticket).id!!)
    }


    @Transactional
    override fun ticketStatusChangeInProgress(ticketId: Int, statusChangeData: TicketStatusChangeInProgressDTO) {
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        val expert = expertRepository.findByIdOrNull(statusChangeData.expertId) ?: throw ExpertNotFoundException()
        ticket.expert = expert
        ticket.priorityLevel = statusChangeData.priorityLevel
        //In this function I try to create a status change and its log, and thrown an exception if it not possible
        ticket.changeStatus(TicketStatus.IN_PROGRESS, UserType.MANAGER, statusChangeData.description)
        ticketRepository.save(ticket)
    }

    override fun ticketStatusChangeGeneric(
        ticketId: Int,
        newStatus: TicketStatus,
        statusChangeData: TicketStatusChangeGenericDTO
    ) {
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        //In this function I try to create a status change and its log, and thrown an exception if it not possible
        ticket.changeStatus(newStatus, statusChangeData.changedBy, statusChangeData.description)
        ticketRepository.save(ticket)
        println("TICKET TIME ${ticket.lastModifiedAt}")
        //TICKETCHANGE TIME IS UPDATED TOO AFTER THE SAVE, BUT IT HAPPENS FEW MILLIS LATER TODO

    }
}