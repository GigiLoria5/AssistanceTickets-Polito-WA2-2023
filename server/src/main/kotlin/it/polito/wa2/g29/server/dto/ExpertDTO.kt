package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Skill


data class ExpertDTO(
    val expertId: Int?,
    val name: String,
    val surname: String,
    val email: String,
    val country: String,
    val city: String,
    val skills: List<SkillDTO>
)

data class SkillDTO(
    val expertise: String,
    val level: String
)

fun Skill.toDTO(): SkillDTO {
    return SkillDTO(expertise = expertise.toString(), level = level.toString())
}

fun Expert.toDTO(): ExpertDTO {
    val skillDTOs = skills.map { it.toDTO() }
    return ExpertDTO(id, name, surname, email, country, city, skills = skillDTOs)
}
