package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.ExpertDTO
import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.dto.TicketDTO


interface ExpertService {
    fun getAllExperts(): List<ExpertDTO>

    fun getExpertById(expertId: Int): ExpertDTO

    fun getAllTicketsByExpertId(expertId: Int): List<TicketDTO>

    fun getTicketStatusChangesByExpertId(expertId: Int): List<TicketChangeDTO>
}