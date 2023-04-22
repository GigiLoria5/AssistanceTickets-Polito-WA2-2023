package it.polito.wa2.g29.server.model


import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@Entity
@Table(name = "tickets")
class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var ticketId: Int? = null

    var title: String = ""

    var description: String = ""

    @ManyToOne
    @JoinColumn(name = "product_id")
    var product: Product? = null

    @ManyToOne
    @JoinColumn(name = "profile_id")
    var customer: Profile? = null

    @ManyToOne
    @JoinColumn(name = "expert_id")
    var expert: Expert? = null

    @OneToOne(mappedBy = "ticket")
    var chat: Chat? = null

    var status: TicketStatus = TicketStatus.OPEN

    var priorityLevel: TicketPriority? = null

    @CreatedDate
    var createdAt: Long = 0

    @LastModifiedDate
    var lastModifiedAt: Long = 0
}