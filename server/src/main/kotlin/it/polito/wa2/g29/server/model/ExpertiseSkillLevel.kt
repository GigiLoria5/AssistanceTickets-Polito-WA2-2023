package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.enums.Expertise
import it.polito.wa2.g29.server.enums.SkillLevel
import jakarta.persistence.*


@Entity
@Table(name = "expertises_skill_levels")

class ExpertiseSkillLevel(

    var expertise: Expertise,
    var skillLevel: SkillLevel

) : EntityBase<Int>()