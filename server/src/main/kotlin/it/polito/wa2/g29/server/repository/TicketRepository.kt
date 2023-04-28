package it.polito.wa2.g29.server.repository

import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.model.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : JpaRepository<Ticket, Int> {
    fun findTicketByCustomerAndProductAndStatusNot(customer: Profile, product: Product, status: TicketStatus): Ticket?
}