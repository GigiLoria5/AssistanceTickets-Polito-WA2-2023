package it.polito.wa2.g29.server.dto.ticketDTOs

import it.polito.wa2.g29.server.enums.TicketPriority
import jakarta.annotation.Nullable
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class ChangeTicketStatusToStartDTO(
    @field:NotNull @field:Min(1)
    val expertId: Int,
    @field:Valid
    val priorityLevel: TicketPriority,
    @field:Nullable
    val description: String?
)