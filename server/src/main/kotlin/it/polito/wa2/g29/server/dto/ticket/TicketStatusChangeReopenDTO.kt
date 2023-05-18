package it.polito.wa2.g29.server.dto.ticket

import jakarta.validation.constraints.NotBlank

data class TicketStatusChangeReopenDTO(
    @field:NotBlank
    val description: String
)