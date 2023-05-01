package it.polito.wa2.g29.server.dto.ticketDTOs

import it.polito.wa2.g29.server.enums.UserType
import jakarta.annotation.Nullable
import jakarta.validation.Valid

data class TicketStatusChangeDTO(
    @field:Valid
    val changedBy: UserType,
    @field:Nullable
    val description: String?
)