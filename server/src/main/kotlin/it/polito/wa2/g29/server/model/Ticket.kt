package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.dto.NewTicketDTO
import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.exception.NotValidStatusChangeException
import it.polito.wa2.g29.server.exception.ProductNotFoundException
import it.polito.wa2.g29.server.exception.ProfileNotFoundException
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.utils.TicketStatusChangeRules
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.repository.findByIdOrNull

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

    @LastModifiedDate
    @Column(nullable = false)
    var lastModifiedAt: Long = createdAt

    fun changeStatus(newStatus: TicketStatus, changedBy: UserType, description: String?) {
        if (!TicketStatusChangeRules.isValidStatusChange(status, newStatus))
            throw NotValidStatusChangeException()
        val oldStatus = status
        status = newStatus
        val ticketChange = TicketChange(this, oldStatus, changedBy, description)
        ticketChanges.add(ticketChange)
    }
}

fun NewTicketDTO.toEntity(
    productRepository: ProductRepository,
    profileRepository: ProfileRepository
): Ticket {
    val product = productRepository.findByIdOrNull(productId) ?: throw ProductNotFoundException()
    val customer = profileRepository.findByIdOrNull(customerId) ?: throw ProfileNotFoundException()
    return Ticket(title, description, product, customer)
}