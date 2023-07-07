package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.dto.TicketDTO
import it.polito.wa2.g29.server.dto.ticket.NewTicketDTO
import it.polito.wa2.g29.server.dto.ticket.NewTicketIdDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.exception.*
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.model.toEntity
import it.polito.wa2.g29.server.repository.*
import it.polito.wa2.g29.server.service.TicketService
import it.polito.wa2.g29.server.utils.AuthenticationUtil
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val productTokenRepository: ProductTokenRepository,
    private val profileRepository: ProfileRepository,
    private val expertRepository: ExpertRepository
) : TicketService {

    private val log = LoggerFactory.getLogger(TicketServiceImpl::class.java)

    override fun getAllTickets(): List<TicketDTO> {
        return ticketRepository.findAll().map { it.toDTO() }
    }

    override fun getTicketsByStatus(status: TicketStatus): List<TicketDTO> {
        return ticketRepository.findTicketsByStatus(status).map { it.toDTO() }
    }

    override fun getTicketById(ticketId: Int): TicketDTO {
        val ticket = ticketRepository.findByIdOrNull(ticketId)
            ?: run {
                log.info("Ticket not found.")
                throw TicketNotFoundException()
            }
        checkUserAuthorisation(ticket)
        return ticket.toDTO()
    }

    override fun getTicketStatusChangesByTicketId(ticketId: Int): List<TicketChangeDTO> {
        val ticket = ticketRepository.findByIdOrNull(ticketId)
            ?: run {
                log.info("Ticket not found.")
                throw TicketNotFoundException()
            }
        checkUserAuthorisation(ticket)
        return ticket.ticketChanges.sortedWith(compareByDescending { it.time }).map { it.toDTO() }
    }

    override fun createTicket(newTicketDTO: NewTicketDTO): NewTicketIdDTO {
        val username = AuthenticationUtil.getUsername()
        val customer = profileRepository.findProfileByEmail(username)!!
        val productToken = productTokenRepository.findByIdOrNull(newTicketDTO.productTokenId)
            ?: throw ProductTokenNotFoundException()
        if (productToken.user!!.id != customer.id)
            throw ProductTokenNotOwnedException("You does not own this productTokenId")
        val ticket = newTicketDTO.toEntity(productToken, customer)

        //throw an exception if a not closed ticket for the same customer and productToken already exists
        if (ticketRepository.findTicketByCustomerAndProductTokenAndStatusNot(
                ticket.customer,
                ticket.productToken,
                TicketStatus.CLOSED
            ) != null
        )
            throw DuplicateTicketException("A not closed ticket with the same customer and productToken already exists")
        return NewTicketIdDTO(ticketRepository.save(ticket).id!!)
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
