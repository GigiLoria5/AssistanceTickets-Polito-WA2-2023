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

    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER)")
    @GetMapping("/tickets")
    fun getAllTickets(
        @RequestParam("status", required = false) status: TicketStatus?
    ): List<TicketDTO> {
        return if (status == null) {
            log.info("Retrieve all tickets")
            ticketService.getAllTickets()
        } else {
            log.info("Retrieve ticket with status: {}", status)
            ticketService.getTicketsByStatus(status)
        }
    }

    @GetMapping("/tickets/{ticketId}")
    fun getTicketById(@PathVariable @Valid @Min(1) ticketId: Int): TicketDTO {
        log.info("Retrieve details of ticket: {}", ticketId)
        return ticketService.getTicketById(ticketId)
    }

    @GetMapping("/tickets/{ticketId}/statusChanges")
    fun getTicketStatusChangesByTicketId(@PathVariable @Valid @Min(1) ticketId: Int): List<TicketChangeDTO> {
        log.info("Retrieve status changes of ticket: {}", ticketId)
        return ticketService.getTicketStatusChangesByTicketId(ticketId)
    }

    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_CLIENT)")
    @PostMapping("/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTicket(@RequestBody @Valid @NotNull ticket: NewTicketDTO): NewTicketIdDTO {
        log.info("Create a new ticket")
        return ticketService.createTicket(ticket)
    }

}
