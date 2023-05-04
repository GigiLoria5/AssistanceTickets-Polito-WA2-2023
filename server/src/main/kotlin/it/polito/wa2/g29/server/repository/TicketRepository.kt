package it.polito.wa2.g29.server.repository

import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.model.Product
import it.polito.wa2.g29.server.model.Profile
import it.polito.wa2.g29.server.model.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface TicketRepository : JpaRepository<Ticket, Int> {

    @Transactional(readOnly = true)
    fun findTicketsByStatus(status: TicketStatus): List<Ticket>

    @Transactional(readOnly = true)
    fun findTicketByCustomerAndProductAndStatusNot(customer: Profile, product: Product, status: TicketStatus): Ticket?

    @Transactional(readOnly = true)
    fun findTicketByCustomerAndProductAndStatusNotAndStatusNot(
        customer: Profile,
        product: Product,
        status1: TicketStatus,
        status2: TicketStatus
    ): Ticket?
}