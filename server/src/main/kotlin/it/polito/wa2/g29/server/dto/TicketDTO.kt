package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.enums.TicketPriority
import it.polito.wa2.g29.server.model.Ticket
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Null


data class TicketDTO(
    val ticketId: Int?,
    val title: String,
    val description: String,
    val productId: Int?,
    val customerId: Int?,
    val expertId: Int?,
    val totalExchangedMessages: Int,
    val status: String,
    val priorityLevel: String?,
    val createdAt: Long,
    val lastModifiedAt: Long
)

data class NewTicketDTO(
    @field:NotNull @field:Min(1)
    val customerId: Int,
    @field:NotNull @field:Min(1)
    val productId: Int,
    @field:NotBlank
    val title: String,
    @field:NotBlank val description: String
)

data class TicketIdDTO(
    val ticketId: Int
)

data class StartTicketDTO(
    @field:NotNull @field:Min(1)
    val expertId: Int,
    @field:Valid
    val priorityLevel: TicketPriority,
    @field:Null
    val description: String?
)

fun Ticket.toDTO(): TicketDTO {

    return TicketDTO(
        id,
        title,
        description,
        product.id,
        customer.id,
        expert?.id,
        messages.count(),
        status.toString(),
        priorityLevel?.toString(),
        createdAt,
        lastModifiedAt
    )
}
