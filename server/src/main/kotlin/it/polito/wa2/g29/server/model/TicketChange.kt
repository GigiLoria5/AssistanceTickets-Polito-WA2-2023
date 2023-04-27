package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate

@Entity
@Table(name = "tickets_changes")
class TicketChange(
    @ManyToOne
    @JoinColumn(updatable = false, nullable = false)
    var ticket: Ticket,
    @Column(updatable = false, nullable = false)
    var oldStatus: TicketStatus,
    @Column(updatable = false, nullable = false)
    var newStatus: TicketStatus,
    @ManyToOne
    @JoinColumn(updatable = false)
    var currentExpert: Expert?,
    @Column(updatable = false)
    var description: String?,
    @Column(updatable = false, nullable = false)
    var changedBy: UserType

) : EntityBase<Int>() {

    @CreatedDate
    @Column(updatable = false, nullable = false)
    var time: Long = System.currentTimeMillis()

}
