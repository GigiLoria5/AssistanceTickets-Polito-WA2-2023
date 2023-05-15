package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.dto.ticket.NewTicketDTO
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@EntityListeners(AuditingEntityListener::class)
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
    var createdAt: Long = 0

    @CreatedDate
    @Column(nullable = false)
    var lastModifiedAt: Long = createdAt

    fun addMessage(msg: Message) {
        msg.ticket = this
        messages.add(msg)
    }

    @PreUpdate
    private fun preUpdate() {
        if (ticketChanges.size == 0) {
            lastModifiedAt = System.currentTimeMillis()
            return
        }
        lastModifiedAt = ticketChanges.maxOf { it.time }
    }

    fun changeStatus(newStatus: TicketStatus, changedBy: UserType, description: String?) {

        val oldStatus = status
        status = newStatus

        val tc = TicketChange(this, oldStatus, changedBy, description)

        if (newStatus == TicketStatus.REOPENED || newStatus == TicketStatus.OPEN)
            expert = null

        ticketChanges.add(tc)
    }
}

fun NewTicketDTO.toEntity(
    product: Product,
    customer: Profile
): Ticket {
    return Ticket(title, description, product, customer)
}