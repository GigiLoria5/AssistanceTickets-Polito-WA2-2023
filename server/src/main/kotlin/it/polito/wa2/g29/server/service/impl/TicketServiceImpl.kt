package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.*
import it.polito.wa2.g29.server.dto.ticket.NewTicketDTO
import it.polito.wa2.g29.server.dto.ticket.NewTicketIdDTO
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.exception.*
import it.polito.wa2.g29.server.model.toEntity
import it.polito.wa2.g29.server.repository.*
import it.polito.wa2.g29.server.service.TicketService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val productRepository: ProductRepository,
    private val profileRepository: ProfileRepository
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
        return ticket.ticketChanges.sortedWith(compareByDescending { it.time }).map { it.toDTO() }
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
}