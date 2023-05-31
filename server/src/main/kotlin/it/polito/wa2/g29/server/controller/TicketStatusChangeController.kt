package it.polito.wa2.g29.server.controller

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeDTO
import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeInProgressDTO
import it.polito.wa2.g29.server.dto.ticket.TicketStatusChangeReopenDTO
import it.polito.wa2.g29.server.enums.TicketStatus
import it.polito.wa2.g29.server.service.TicketStatusChangeService
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
class TicketStatusChangeController(private val ticketStatusChangeService: TicketStatusChangeService) {
    private val log = LoggerFactory.getLogger(TicketStatusChangeController::class.java)

    // PUT /API/tickets/{ticketId}/start -Allows to start the progress of an "OPEN"/"REOPENED" ticket. The ticket status will be "IN_PROGRESS"
    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER)")
    @PutMapping("/tickets/{ticketId}/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun startTicket(
        @PathVariable @Valid @Min(1) ticketId: Int,
        @RequestBody @Valid @NotNull statusChangeData: TicketStatusChangeInProgressDTO
    ) {
        log.info("Change status into IN_PROGRESS for ticket: {} and assign it to expert: {} ",ticketId,statusChangeData.expertId)
        ticketStatusChangeService.ticketStatusChangeInProgress(ticketId, statusChangeData)
    }

    // PUT /API/tickets/{ticketId}/stop -Allows to stop the progress of an "IN_PROGRESS" ticket. The ticket status will be "OPEN"
    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER) or hasAuthority(@AuthUtil.ROLE_EXPERT)")
    @PutMapping("/tickets/{ticketId}/stop")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun stopTicket(
        @PathVariable @Valid @Min(1) ticketId: Int,
        @RequestBody @Valid @NotNull statusChangeData: TicketStatusChangeDTO
    ) {
        log.info("Change status into OPEN for ticket: {}", ticketId)
        ticketStatusChangeService.ticketStatusChange(ticketId, TicketStatus.OPEN, statusChangeData)
    }

    // PUT /API/tickets/{ticketId}/resolve -Allows to resolve the progress of an "OPEN"/"REOPENED"/"IN_PROGRESS" ticket. The ticket status will be "RESOLVED"
    @PutMapping("/tickets/{ticketId}/resolve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun resolveTicket(
        @PathVariable @Valid @Min(1) ticketId: Int,
        @RequestBody @Valid @NotNull statusChangeData: TicketStatusChangeDTO
    ) {
        log.info("Change status into RESOLVED for ticket: {}", ticketId)
        ticketStatusChangeService.ticketStatusChange(ticketId, TicketStatus.RESOLVED, statusChangeData)
    }

    // PUT /API/tickets/{ticketId}/reopen -Allows to reopen a "CLOSED"/"RESOLVED" ticket. The ticket status will be "REOPENED"
    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_CLIENT)")
    @PutMapping("/tickets/{ticketId}/reopen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun reopenTicket(
        @PathVariable @Valid @Min(1) ticketId: Int,
        @RequestBody @Valid @NotNull statusChangeData: TicketStatusChangeReopenDTO
    ) {
        log.info("Change status into REOPENED for ticket: {}", ticketId)
        ticketStatusChangeService.ticketStatusChange(
            ticketId,
            TicketStatus.REOPENED,
            TicketStatusChangeDTO(statusChangeData.description)
        )
    }

    // PUT /API/tickets/{ticketId}/close -Allows to close a "OPEN"/"RESOLVED"/"IN_PROGRESS"/"REOPENED" ticket. The ticket status will be "CLOSED"
    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER) or hasAuthority(@AuthUtil.ROLE_EXPERT)")
    @PutMapping("/tickets/{ticketId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun closeTicket(
        @PathVariable @Valid @Min(1) ticketId: Int,
        @RequestBody @Valid @NotNull statusChangeData: TicketStatusChangeDTO
    ) {
        log.info("Change status into CLOSED for ticket: {}", ticketId)
        ticketStatusChangeService.ticketStatusChange(ticketId, TicketStatus.CLOSED, statusChangeData)
    }

}
