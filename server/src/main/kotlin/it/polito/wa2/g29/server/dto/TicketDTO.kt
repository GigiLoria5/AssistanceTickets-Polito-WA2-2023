package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Ticket

data class TicketDTO(
    val ticketId: Int?,
    val title: String,
    val description: String,
    val productId: Int?,
    val productTokenId: Int?,
    val customerId: Int?,
    val expertId: Int?,
    val totalExchangedMessages: Int,
    val status: String,
    val priorityLevel: String?,
    val createdAt: Long,
    val lastModifiedAt: Long
)

fun Ticket.toDTO(): TicketDTO {
    return TicketDTO(
        id,
        title,
        description,
        product.id,
        productToken.id,
        customer.id,
        expert?.id,
        messages.count(),
        status.toString(),
        priorityLevel?.toString(),
        createdAt,
        lastModifiedAt
    )
}
