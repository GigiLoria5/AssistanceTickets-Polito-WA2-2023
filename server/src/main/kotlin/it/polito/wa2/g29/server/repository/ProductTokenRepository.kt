package it.polito.wa2.g29.server.repository

import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.model.ProductToken
import it.polito.wa2.g29.server.model.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface ProductTokenRepository : JpaRepository<ProductToken, Int> {

    @Transactional(readOnly = true)
    fun findProductTokenByToken(token: String):ProductToken?

}
