package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@Entity
@Table(
    name = "tickets",
    uniqueConstraints =
    [UniqueConstraint(columnNames = arrayOf("product_id", "customer_id", "created_at"))]
)
class Ticket(
    @Column(nullable = false)
    var title: String,
    @Column(nullable = false)
    var description: String,
    @ManyToOne
    @JoinColumn(updatable = false, nullable = false)
    var product: Product,
    @ManyToOne
    @JoinColumn(updatable = false, nullable = false)
    var customer: Profile
) : EntityBase<Int>() {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TicketStatus = TicketStatus.OPEN

    @Enumerated(EnumType.STRING)
    var priorityLevel: TicketPriority? = null

    @ManyToOne
    var expert: Expert? = null

    @OneToMany(mappedBy = "ticket", cascade = [CascadeType.ALL])
    var messages = mutableSetOf<Message>()

    @OneToMany(mappedBy = "ticket", cascade = [CascadeType.ALL])
    var ticketChanges = mutableSetOf<TicketChange>()

    @CreatedDate
    @Column(updatable = false, nullable = false, name = "created_at")
    var createdAt: Long = System.currentTimeMillis()

    @LastModifiedDate
    @Column(nullable = false)
    var lastModifiedAt: Long = createdAt
}
