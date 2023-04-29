package it.polito.wa2.g29.server.controller

import it.polito.wa2.g29.server.dto.*
import it.polito.wa2.g29.server.service.TicketService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

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

    // POST /API/tickets -- create a new ticket or fail if some field is missing, or is not valid, or does not has correspondence or in case of duplicates
    @PostMapping("/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTicket(@RequestBody @Valid @NotNull ticket: NewTicketDTO): TicketIdDTO {
        return ticketService.createTicket(ticket)
    }

    // PUT /API/tickets/{ticketId}/start -Allows to start the progress of an "OPEN"/"REOPENED" ticket. The ticket status will be "IN_PROGRESS"
    @PutMapping("/tickets/{ticketId}/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun startTicket(
        @PathVariable @Valid @Min(1) ticketId: Int,
        @RequestBody @Valid @NotNull statusChangeData: StartTicketDTO
    ) {
        ticketService.startTicket(ticketId, statusChangeData)
    }

    // PUT /API/tickets/{ticketId}/stop -Allows to stop the progress of an "IN_PROGRESS" ticket. The ticket status will be "OPEN"
    @PutMapping("/tickets/{ticketId}/stop")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun stopTicket(
        @PathVariable @Valid @Min(1) ticketId: Int,
        @RequestBody @Valid @NotNull statusChangeData: ChangeTicketStatusDTO
    ) {
        //ticketService.stopTicket(ticketId,statusChangeData)
    }

}