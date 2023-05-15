package it.polito.wa2.g29.server.dto.ticket

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class NewTicketDTO(
    @field:NotNull @field:Min(1)
    val productId: Int,
    @field:NotBlank
    val title: String,
    @field:NotBlank
    val description: String
)