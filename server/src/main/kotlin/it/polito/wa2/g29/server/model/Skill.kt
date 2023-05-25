package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.dto.SkillDTO
import it.polito.wa2.g29.server.enums.Expertise
import it.polito.wa2.g29.server.enums.Level
import jakarta.persistence.*

@Entity
@Table(
    name = "skills",
    uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("expertise", "expert_id"))]
)
class Skill(
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var expertise: Expertise,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var level: Level,
    @ManyToOne(fetch = FetchType.LAZY)
    var expert: Expert
) : EntityBase<Int>()

fun SkillDTO.toEntity(expert: Expert) : Skill {
    return Skill(Expertise.valueOf(expertise), Level.valueOf(level), expert)
}