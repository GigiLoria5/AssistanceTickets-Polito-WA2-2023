package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.ExpertDTO

interface ExpertService {
    fun getAllExperts(): List<ExpertDTO>

    fun getExpertById(expertId: Int): ExpertDTO
}