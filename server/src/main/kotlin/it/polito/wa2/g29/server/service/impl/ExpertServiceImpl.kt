package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.ExpertDTO
import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.dto.TicketDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.exception.ExpertNotFoundException
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.service.ExpertService
import it.polito.wa2.g29.server.utils.AuthenticationUtil
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class ExpertServiceImpl(private val expertRepository: ExpertRepository) : ExpertService {
    override fun getAllExperts(): List<ExpertDTO> {
        return expertRepository.findAll().map { it.toDTO() }
    }

    override fun getExpertById(expertId: Int): ExpertDTO {
        val expert = getExpertObject(expertId)
        return expert.toDTO()
    }

    override fun getAllTicketsByExpertId(expertId: Int): List<TicketDTO> {
        val expert = getExpertObject(expertId)
        return expert.tickets.sortedWith(compareByDescending { it.priorityLevel }).map { it.toDTO() }
    }

    override fun getTicketStatusChangesByExpertId(expertId: Int): List<TicketChangeDTO> {
        val expert = getExpertObject(expertId)
        return expert.ticketChanges.filter { it.changedBy == UserType.EXPERT }
            .sortedWith(compareByDescending { it.time }).map { it.toDTO() }
    }

    private fun getExpertObject(expertId: Int): Expert {
        val username = AuthenticationUtil.getUsername()
        return when(AuthenticationUtil.getUserTypeEnum()) {
            UserType.EXPERT -> {
                val expert = expertRepository.findExpertByEmail(username)!!
                if (expert.id != expertId)
                    throw AccessDeniedException("")
                expert
            }
            //ROLE_MANAGER
            else -> expertRepository.findByIdOrNull(expertId) ?: throw ExpertNotFoundException()
        }
    }
}
