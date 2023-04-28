package it.polito.wa2.g29.server.controller

import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.dto.TicketDTO
import it.polito.wa2.g29.server.service.TicketService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/API")
@Validated
@RestController
class TicketController(
    private val ticketService: TicketService
) {
    // GET /API/tickets/ -- list all tickets in the DB
    @GetMapping("/tickets")
    fun getAllTickets(): List<TicketDTO> {
        return ticketService.getAllTickets()
    }

    // GET /API/tickets/{ticketId} -- details of ticket {ticketId} or fail if it does not exist
    @GetMapping("/tickets/{ticketId}")
    fun getTicketById(@PathVariable @Valid @Min(1) ticketId: Int): TicketDTO {
        return ticketService.getTicketById(ticketId)
    }

    // GET /API/tickets/{ticketId}/statusChanges -- details of ticket {ticketId} status changes or fail if {ticketId} does not exist
    @GetMapping("/tickets/{ticketId}/statusChanges")
    fun getTicketStatusChangesByTicketId(@PathVariable @Valid @Min(1) ticketId: Int): List<TicketChangeDTO> {
        return ticketService.getTicketStatusChangesByTicketId(ticketId)
    }
}