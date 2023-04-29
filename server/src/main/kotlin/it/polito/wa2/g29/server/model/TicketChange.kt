package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import jakarta.persistence.*

@Entity
@Table(
    name = "tickets_changes",
    uniqueConstraints =
    [UniqueConstraint(columnNames = arrayOf("ticket_id", "time"))]
)
class TicketChange(
    @ManyToOne
    @JoinColumn(updatable = false, nullable = false)
    var ticket: Ticket,
    @Column(updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    var oldStatus: TicketStatus,
    @Column(updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    var changedBy: UserType,
    @Column(updatable = false)
    var description: String?

) : EntityBase<Int>() {
    @Column(updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    var newStatus: TicketStatus = ticket.status

    @ManyToOne
    @JoinColumn(updatable = false)
    var currentExpert: Expert? = ticket.expert

    @Column(updatable = false, nullable = false)
    var time: Long = ticket.lastModifiedAt

}
