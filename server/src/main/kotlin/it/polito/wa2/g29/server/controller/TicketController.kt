package it.polito.wa2.g29.server.controller

import it.polito.wa2.g29.server.dto.*
import it.polito.wa2.g29.server.dto.ticketDTOs.*
import it.polito.wa2.g29.server.enums.TicketStatus
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
    fun getAllTickets(
        @RequestParam("status", required = false) status: TicketStatus?
    ): List<TicketDTO> {
        return if (status == null)
            ticketService.getAllTickets()
        else
            ticketService.getTicketsByStatus(status)
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
    fun createTicket(@RequestBody @Valid @NotNull ticket: NewTicketDTO): NewTicketIdDTO {
        return ticketService.createTicket(ticket)
    }

    // PUT /API/tickets/{ticketId}/start -Allows to start the progress of an "OPEN"/"REOPENED" ticket. The ticket status will be "IN_PROGRESS"
    @PutMapping("/tickets/{ticketId}/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun startTicket(
        @PathVariable @Valid @Min(1) ticketId: Int,
        @RequestBody @Valid @NotNull statusChangeData: TicketStatusChangeInProgressDTO
    ) {
        ticketService.ticketStatusChangeInProgress(ticketId, statusChangeData)
    }

    // PUT /API/tickets/{ticketId}/stop -Allows to stop the progress of an "IN_PROGRESS" ticket. The ticket status will be "OPEN"
    @PutMapping("/tickets/{ticketId}/stop")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun stopTicket(
        @PathVariable @Valid @Min(1) ticketId: Int,
        @RequestBody @Valid @NotNull statusChangeData: TicketStatusChangeGenericDTO
    ) {
        ticketService.ticketStatusChangeGeneric(ticketId, TicketStatus.OPEN, statusChangeData)
    }

    // PUT /API/tickets/{ticketId}/resolve -Allows to resolve the progress of an "OPEN"/"REOPENED"/"IN_PROGRESS" ticket. The ticket status will be "RESOLVED"
    @PutMapping("/tickets/{ticketId}/resolve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun resolveTicket(
        @PathVariable @Valid @Min(1) ticketId: Int,
        @RequestBody @Valid @NotNull statusChangeData: TicketStatusChangeGenericDTO
    ) {
        ticketService.ticketStatusChangeGeneric(ticketId, TicketStatus.RESOLVED, statusChangeData)
    }

    // PUT /API/tickets/{ticketId}/reopen -Allows to reopen a "CLOSED"/"RESOLVED" ticket. The ticket status will be "REOPENED"
    @PutMapping("/tickets/{ticketId}/reopen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun reopenTicket(
        @PathVariable @Valid @Min(1) ticketId: Int,
        @RequestBody @Valid @NotNull statusChangeData: TicketStatusChangeReopenDTO
    ) {

        ticketService.ticketStatusChangeGeneric(
            ticketId,
            TicketStatus.REOPENED,
            TicketStatusChangeGenericDTO(statusChangeData.changedBy, statusChangeData.description)
        )
    }

    // PUT /API/tickets/{ticketId}/close -Allows to close a "OPEN"/"RESOLVED"/"IN_PROGRESS"/"REOPENED" ticket. The ticket status will be "CLOSED"
    @PutMapping("/tickets/{ticketId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun closeTicket(
        @PathVariable @Valid @Min(1) ticketId: Int,
        @RequestBody @Valid @NotNull statusChangeData: TicketStatusChangeGenericDTO
    ) {
        ticketService.ticketStatusChangeGeneric(ticketId, TicketStatus.CLOSED, statusChangeData)
    }
}