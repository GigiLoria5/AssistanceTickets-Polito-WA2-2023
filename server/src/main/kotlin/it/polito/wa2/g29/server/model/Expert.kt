package it.polito.wa2.g29.server.model

import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "experts")
class Expert(
    var name: String,
    var surname: String,
    var email: String,
    @OneToMany
     var expertiseSkillLevels: MutableSet<ExpertiseSkillLevel> = mutableSetOf()
) : EntityBase<Int>(){

    @OneToMany(mappedBy = "expert")
    var tickets: MutableSet<Ticket> = mutableSetOf()

    fun addExpertiseSkill(e: ExpertiseSkillLevel){
        expertiseSkillLevels.add(e)
    }
}