package it.polito.wa2.g29.server.dto.ticket

import it.polito.wa2.g29.server.enums.UserType
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

data class TicketStatusChangeReopenDTO(
    @field:Valid
    val changedBy: UserType,
    @field:NotBlank
    val description: String
)