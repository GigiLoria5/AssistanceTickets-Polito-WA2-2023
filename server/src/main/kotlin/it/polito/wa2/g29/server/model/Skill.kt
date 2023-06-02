package it.polito.wa2.g29.server.model

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
) : EntityBase<Int>() {
    override fun equals(other: Any?): Boolean {
        if (other === null) return false
        if (other === this) return true
        if (other !is Skill) return false
        val that = other as Skill
        return this.expertise == that.expertise && this.level == that.level && this.expert.email == that.expert.email
    }
    override fun hashCode(): Int {
        return 5
    }
}