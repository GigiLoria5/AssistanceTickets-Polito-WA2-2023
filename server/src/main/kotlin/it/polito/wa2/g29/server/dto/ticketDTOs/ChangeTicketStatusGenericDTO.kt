package it.polito.wa2.g29.server.dto.ticketDTOs

import it.polito.wa2.g29.server.enums.UserType
import jakarta.validation.Valid
import jakarta.validation.constraints.Null

data class ChangeTicketStatusGenericDTO(
    @field:Valid
    val changedBy: UserType,
    @field:Null
    val description: String?
)