package it.polito.wa2.g29.server.dto.ticket

import jakarta.annotation.Nullable

data class TicketStatusChangeDTO(
    @field:Nullable
    val description: String?
)