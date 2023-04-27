package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.ExpertDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.service.ExpertService
import org.springframework.stereotype.Service

@Service
class ExpertServiceImpl(private val expertRepository: ExpertRepository): ExpertService {
    override fun getAllExperts(): List<ExpertDTO> {
        return expertRepository.findAll().map { it.toDTO() }
    }
}