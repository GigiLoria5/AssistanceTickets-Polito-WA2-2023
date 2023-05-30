package it.polito.wa2.g29.server.controller

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g29.server.dto.*
import it.polito.wa2.g29.server.dto.ticket.*
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.service.TicketService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*



@RequestMapping("/API")
@Validated
@RestController
@Observed
class TicketController(private val ticketService: TicketService) {

    private val log = LoggerFactory.getLogger(TicketController::class.java)
    // GET /API/tickets -- list all tickets in the DB
    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER)")
    @GetMapping("/tickets")
    fun getAllTickets(
        @RequestParam("status", required = false) status: TicketStatus?
    ): List<TicketDTO> {
        return if (status == null) {
            log.info("Retrieve all tickets")
            ticketService.getAllTickets()
        }
        else {
            log.info("Retrieve ticket with status: {}", status)
            ticketService.getTicketsByStatus(status)
        }
    }

    // GET /API/tickets/{ticketId} -- details of ticket {ticketId} or fail if it does not exist
    @GetMapping("/tickets/{ticketId}")
    fun getTicketById(@PathVariable @Valid @Min(1) ticketId: Int): TicketDTO {
        log.info("Retrieve details of ticket: {}", ticketId)
        return ticketService.getTicketById(ticketId)
    }

    // GET /API/tickets/{ticketId}/statusChanges -- details of ticket {ticketId} status changes or fail if {ticketId} does not exist
    @GetMapping("/tickets/{ticketId}/statusChanges")
    fun getTicketStatusChangesByTicketId(@PathVariable @Valid @Min(1) ticketId: Int): List<TicketChangeDTO> {
        log.info("Retrieve status changes of ticket: {}", ticketId)
        return ticketService.getTicketStatusChangesByTicketId(ticketId)
    }

    // POST /API/tickets -- create a new ticket or fail if some field is missing, or is not valid, or does not has correspondence or in case of duplicates
    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_CLIENT)")
    @PostMapping("/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTicket(@RequestBody @Valid @NotNull ticket: NewTicketDTO): NewTicketIdDTO {
        log.info("Create a new ticket")
        return ticketService.createTicket(ticket)
    }
}
