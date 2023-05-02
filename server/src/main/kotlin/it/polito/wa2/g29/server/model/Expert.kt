package it.polito.wa2.g29.server.model

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

    @PreRemove
    private fun preRemove() {
        tickets.forEach { it.expert = null }
        messages.forEach { it.expert = null }
        ticketChanges.forEach { it.currentExpert = null }
    }
}