package it.polito.wa2.g29.server.repository

import it.polito.wa2.g29.server.model.Skill
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SkillRepository : JpaRepository<Skill, Int> {
}