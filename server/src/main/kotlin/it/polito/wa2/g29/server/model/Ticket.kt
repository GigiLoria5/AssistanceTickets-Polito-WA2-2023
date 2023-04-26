package it.polito.wa2.g29.server.model


import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@Entity
@Table(name = "tickets")
class Ticket(
    var title: String,
    var description: String,
    @ManyToOne
    var product: Product,
    @ManyToOne
    var customer: Profile,
) : EntityBase<Int>() {
    var status: TicketStatus = TicketStatus.OPEN
    var priorityLevel: TicketPriority? = null

    @ManyToOne
    var expert: Expert? = null

    @OneToMany(mappedBy = "ticket")
    var messages: MutableSet<Message> = mutableSetOf()

    @OneToMany(mappedBy = "ticket")
    var ticketChanges: MutableSet<TicketChange> = mutableSetOf()

    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: Long = System.currentTimeMillis()

    @LastModifiedDate
    @Column(nullable = false)
    var lastModifiedAt: Long = createdAt
}
