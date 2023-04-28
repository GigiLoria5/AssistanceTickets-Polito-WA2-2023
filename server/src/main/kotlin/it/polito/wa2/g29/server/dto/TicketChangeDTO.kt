package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.*


data class TicketChangeDTO(
    val ticketId: Int?,
    val currentExpertId: Int?,
    val oldStatus: String,
    val newStatus: String,
    val changedBy: String,
    val description: String?,
    val time: Long
)

fun TicketChange.toDTO(): TicketChangeDTO {
    return TicketChangeDTO(
        ticket.id,
        currentExpert?.id,
        oldStatus.toString(),
        newStatus.toString(),
        changedBy.toString(),
        description,
        time
    )
}