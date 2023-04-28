package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.*
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.exception.*
import it.polito.wa2.g29.server.model.TicketChange
import it.polito.wa2.g29.server.model.toEntity
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val productRepository: ProductRepository,
    private val profileRepository: ProfileRepository,
    private val expertRepository:ExpertRepository
) : TicketService {
    override fun getAllTickets(): List<TicketDTO> {
        return ticketRepository.findAll().map { it.toDTO() }
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

    override fun createTicket(newTicketDTO: NewTicketDTO):TicketIdDTO {

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
        return TicketIdDTO(ticketRepository.save(ticket).id!!)
    }


    override fun startTicket(ticketId: Int, startTicketDTO: StartTicketDTO) {

        val expert = expertRepository.findByIdOrNull(startTicketDTO.expertId) ?: throw ExpertNotFoundException()

        //validazione stringa
       // TicketPriority.valueOf(startTicketDTO.priorityLevel)
        val x=TicketPriority.values().map { it.toString() }.contains(startTicketDTO.priorityLevel.uppercase(Locale.getDefault()))
        println(x)

    }

}