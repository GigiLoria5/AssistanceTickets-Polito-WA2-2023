package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.dto.TicketDTO
import it.polito.wa2.g29.server.dto.ticket.NewTicketDTO
import it.polito.wa2.g29.server.dto.ticket.NewTicketIdDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.exception.DuplicateTicketException
import it.polito.wa2.g29.server.exception.ProductNotFoundException
import it.polito.wa2.g29.server.exception.TicketNotFoundException
import it.polito.wa2.g29.server.model.toEntity
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.repository.TicketRepository
import it.polito.wa2.g29.server.service.TicketService
import it.polito.wa2.g29.server.utils.AuthenticationUtil
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.AccessDeniedException
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
        if (AuthenticationUtil.isExpert() || AuthenticationUtil.isClient())
            checkExpertOrClientIsAssociatedWithTicket(ticketId)
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        return ticket.toDTO()
    }

    override fun getTicketStatusChangesByTicketId(ticketId: Int): List<TicketChangeDTO> {
        if (AuthenticationUtil.isExpert() || AuthenticationUtil.isClient())
            checkExpertOrClientIsAssociatedWithTicket(ticketId)
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        return ticket.ticketChanges.sortedWith(compareByDescending { it.time }).map { it.toDTO() }
    }

    override fun createTicket(newTicketDTO: NewTicketDTO): NewTicketIdDTO {
        val username = AuthenticationUtil.getUsername()
        val customer = profileRepository.findProfileByEmail(username)!!
        val product = productRepository.findByIdOrNull(newTicketDTO.productId) ?: throw ProductNotFoundException()
        val ticket = newTicketDTO.toEntity(product, customer)

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

    private fun checkExpertOrClientIsAssociatedWithTicket(ticketId: Int) {
        val username = AuthenticationUtil.getUsername()
        if (
            (AuthenticationUtil.isExpert() && !expertHasTicket(ticketId, username))
            ||
            (AuthenticationUtil.isClient() && !clientHasTicket(ticketId, username))
        )
            throw AccessDeniedException("")
    }

    private fun expertHasTicket(ticketId: Int, username: String): Boolean {
        val expert = expertRepository.findExpertByEmail(username)!!
        return expert.tickets.any {
            it.id == ticketId
        }
    }

    private fun clientHasTicket(ticketId: Int, username: String): Boolean {
        val client = profileRepository.findProfileByEmail(username)!!
        return client.tickets.any {
            it.id == ticketId
        }
    }

}
