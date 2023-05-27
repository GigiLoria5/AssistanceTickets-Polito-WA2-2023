package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.config.KeycloakProperties
import it.polito.wa2.g29.server.dto.*
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.exception.DuplicateExpertException
import it.polito.wa2.g29.server.exception.DuplicateSkillInExpertException
import it.polito.wa2.g29.server.exception.ExpertNotFoundException
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.toEntity
import it.polito.wa2.g29.server.repository.ExpertRepository
import it.polito.wa2.g29.server.service.ExpertService
import it.polito.wa2.g29.server.utils.AuthenticationUtil
import it.polito.wa2.g29.server.utils.AuthenticationUtil.KEYCLOAK_ROLE_EXPERT
import it.polito.wa2.g29.server.utils.KeycloakUtil.insertUserInKeycloak
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ExpertServiceImpl(private val expertRepository: ExpertRepository,
                        private val keycloakProperties: KeycloakProperties) : ExpertService {

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

    @Transactional
    override fun createExpert(createExpertDTO: CreateExpertDTO) {
        if (!createExpertDTO.validateExpertSkillsList())
            throw DuplicateSkillInExpertException()

        //it will check that an expert with these email/phone number does not already exist (if exists, it will throw an exception)
        if (expertRepository.findExpertByEmail(createExpertDTO.email) != null)
            throw DuplicateExpertException("an expert with the same email already exists")

        val email = createExpertDTO.email
        val password = createExpertDTO.password

        // insert user in keycloak (it will throw an exception if not possible)
        insertUserInKeycloak(keycloakProperties, email, password, KEYCLOAK_ROLE_EXPERT)

        //insert user in our System
        val expert = createExpertDTO.toEntity()
        expertRepository.save(expert)
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
