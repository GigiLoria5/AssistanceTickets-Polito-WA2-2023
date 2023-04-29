package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.*
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.exception.*
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.model.TicketChange
import it.polito.wa2.g29.server.model.toEntity
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketService
import it.polito.wa2.g29.server.utils.TicketStatusChangeRules
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.Calendar

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

    override fun createTicket(newTicketDTO: NewTicketDTO): TicketIdDTO {

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

        //FORSE VA FATTO IN UNA TRANSACTION. CONTROLLA MEGLIO

        //prendi il ticket
        var ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        //prendi expert
        val expert = expertRepository.findByIdOrNull(startTicketDTO.expertId) ?: throw ExpertNotFoundException()


        //assegna expert a ticket
        ticket.expert = expert
        //assegne priority level
        var createdAt = Calendar.getInstance()
        createdAt.timeInMillis = ticket.createdAt

        var lastMoodifiedAt = Calendar.getInstance()
        lastMoodifiedAt.timeInMillis = ticket.lastModifiedAt

        println("CREATED ${createdAt.time} , LAST MODIFIED ${lastMoodifiedAt.time} ")
        ticket.priorityLevel = startTicketDTO.priorityLevel
        //crea log
        addStatusChange(ticket, TicketStatus.IN_PROGRESS, UserType.MANAGER, startTicketDTO.description)
        ticketRepository.save(ticket)
        ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        createdAt = Calendar.getInstance()
        createdAt.timeInMillis = ticket.createdAt

        lastMoodifiedAt = Calendar.getInstance()
        lastMoodifiedAt.timeInMillis = ticket.lastModifiedAt
        println("CREATED ${createdAt.time} , LAST MODIFIED ${lastMoodifiedAt.time} ")

    }

    private fun addStatusChange(ticket: Ticket, newStatus: TicketStatus, changedBy: UserType, description: String?) {
        //FORSE è MEGLIO METTERLA IN TICKETCHANGE
        //controlla che si possa fare il passaggio
        if (!TicketStatusChangeRules.isValidStatusChange(ticket.status, newStatus))
            throw NotValidStatusChangeException()
        val oldStatus = ticket.status
        ticket.status = newStatus
        //crea il ticketChange e poi cambiagli il tempo
        val ticketChange = TicketChange(ticket, oldStatus, ticket.status, ticket.expert, description, changedBy)
        ticket.ticketChanges.add(ticketChange)
    }
}