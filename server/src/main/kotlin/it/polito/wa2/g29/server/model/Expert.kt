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
    var skills: MutableSet<Skill>
) : EntityBase<Int>() {

    @OneToMany(mappedBy = "expert")
    var tickets = mutableSetOf<Ticket>()

    @OneToMany(mappedBy = "expert")
    var messages = mutableSetOf<Message>()

    @OneToMany(mappedBy = "currentExpert")
    var ticketChanges = mutableSetOf<TicketChange>()

    fun addMessage(m: Message) {
        m.expert = this
        messages.add(m)
    }
}