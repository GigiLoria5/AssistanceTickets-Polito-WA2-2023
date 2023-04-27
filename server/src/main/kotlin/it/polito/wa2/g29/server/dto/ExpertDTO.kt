package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Expert


data class ExpertDTO(
    val expertId: Int?,
    val name: String,
    val surname: String,
    val email: String,
    val skills: List<SkillDTO>
)

data class SkillDTO(
    val expertise: String,
    val level: String
)


fun Expert.toDTO(): ExpertDTO {
    return ExpertDTO(
        id,name,surname,email,
        skills=skills.map{SkillDTO(expertise = it.expertise.name,level=it.level.name  )  })
}
