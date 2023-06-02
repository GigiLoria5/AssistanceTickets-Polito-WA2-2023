package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.dto.CreateExpertDTO
import it.polito.wa2.g29.server.enums.Expertise
import it.polito.wa2.g29.server.enums.Level
import jakarta.persistence.*

@Entity
@Table(
    name = "experts",
    uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("email"))]
)
class Expert(
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var surname: String,
    @Column(nullable = false, unique = true)
    var email: String,
    @Column(nullable = false)
    var country: String,
    @Column(nullable = false)
    var city: String,
    @OneToMany(mappedBy = "expert", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var skills: MutableSet<Skill>
) : EntityBase<Int>() {

    @OneToMany(mappedBy = "expert")
    var tickets = mutableSetOf<Ticket>()

    @OneToMany(mappedBy = "expert")
    var messages = mutableSetOf<Message>()

    @OneToMany(mappedBy = "currentExpert")
    var ticketChanges = mutableSetOf<TicketChange>()

    fun addTicket(ticket: Ticket) {
        ticket.expert = this
        tickets.add(ticket)
    }

    fun addMessage(msg: Message) {
        msg.expert = this
        messages.add(msg)
    }

    fun addSkill(expertise: Expertise, level: Level) {
        val skill = Skill(expertise, level, this)
        skills.add(skill)
    }

    @PreRemove
    private fun preRemove() {
        tickets.forEach { it.expert = null }
        messages.forEach { it.expert = null }
        ticketChanges.forEach { it.currentExpert = null }
    }
}

fun CreateExpertDTO.toEntity(): Expert {
    val expert = Expert(name, surname, email, country, city, mutableSetOf())
    skills.forEach {
        expert.addSkill(it.expertise, it.level)
    }
    return expert
}
