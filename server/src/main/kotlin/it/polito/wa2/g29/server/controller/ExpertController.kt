package it.polito.wa2.g29.server.controller

import it.polito.wa2.g29.server.dto.ExpertDTO
import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.dto.TicketDTO
import it.polito.wa2.g29.server.service.ExpertService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/API")
@Validated
@RestController
class ExpertController(private val expertService: ExpertService) {

    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER)")
    @GetMapping("/experts")
    fun getAllExperts(): List<ExpertDTO> {
        return expertService.getAllExperts()
    }

    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER) or hasAuthority(@AuthUtil.ROLE_EXPERT)")
    @GetMapping("/experts/{expertId}")
    fun getExpertById(@PathVariable @Valid @Min(1) expertId: Int): ExpertDTO? {
        return expertService.getExpertById(expertId)
    }

    // GET /API/experts/{expertId}/tickets -- list all tickets assigned to an expert {expertId} or fail if it does not exist
    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER) or hasAuthority(@AuthUtil.ROLE_EXPERT)")
    @GetMapping("/experts/{expertId}/tickets")
    fun getAllTicketsByExpertId(@PathVariable @Valid @Min(1) expertId: Int): List<TicketDTO> {
        return expertService.getAllTicketsByExpertId(expertId)
    }

    // GET /API/experts/{expertId}/statusChanges -- details of tickets status changes done by an expert {expertId} or fail if it does not exist
    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER) or hasAuthority(@AuthUtil.ROLE_EXPERT)")
    @GetMapping("/experts/{expertId}/statusChanges")
    fun getTicketStatusChangesByExpertId(@PathVariable @Valid @Min(1) expertId: Int): List<TicketChangeDTO> {
        return expertService.getTicketStatusChangesByExpertId(expertId)
    }

}
