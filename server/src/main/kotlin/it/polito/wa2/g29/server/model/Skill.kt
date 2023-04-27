package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.enums.Expertise
import it.polito.wa2.g29.server.enums.Level
import jakarta.persistence.*


@Entity
@Table(name = "skills")

class Skill(

    var expertise: Expertise,
    var level: Level,
    @ManyToOne
    var expert: Expert

) : EntityBase<Int>()