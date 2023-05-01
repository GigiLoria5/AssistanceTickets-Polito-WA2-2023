package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.dto.ticketDTOs.NewTicketDTO
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

    @Column(nullable = false)
    var lastModifiedAt: Long = createdAt

    @PreUpdate
    private fun preUpdate() {
        lastModifiedAt = ticketChanges.maxOf { it.time }
    }

    fun changeStatus(newStatus: TicketStatus, changedBy: UserType, description: String?) {
        if (!TicketStatusChangeRules.isValidStatusChange(status, newStatus))
            throw NotValidStatusChangeException("Could not ${TicketStatusChangeRules.getTaskToAchieveStatus(newStatus)} the ticket with id $id because its current status is '$status'")

        val oldStatus = status
        status = newStatus

        val tc = TicketChange(this, oldStatus, changedBy, description)

        if (newStatus == TicketStatus.REOPENED || newStatus == TicketStatus.OPEN)
            expert = null

        ticketChanges.add(tc)
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