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
    var country: String,
    var city: String,
    @OneToMany(mappedBy = "expert")
     var skills: MutableSet<Skill> = mutableSetOf()
) : EntityBase<Int>(){

    @OneToMany(mappedBy = "expert")
    var tickets: MutableSet<Ticket> = mutableSetOf()

    @OneToMany(mappedBy = "expert")
    var messages: MutableSet<Message> = mutableSetOf()

    @OneToMany(mappedBy = "currentExpert")
    var ticketChanges: MutableSet<TicketChange> = mutableSetOf()

    fun addSkill(s: Skill){
        s.expert=this;
        skills.add(s)
    }
    fun addMessage(m: Message){
        m.expert=this;
        messages.add(m)
    }
}