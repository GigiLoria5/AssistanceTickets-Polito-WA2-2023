package it.polito.wa2.g29.server.controller

import it.polito.wa2.g29.server.dto.CreateExpertDTO
import it.polito.wa2.g29.server.dto.ExpertDTO
import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.dto.TicketDTO
import it.polito.wa2.g29.server.service.ExpertService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

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

    // POST /API/experts/createExpert -- create a new expert or fail if some field is missing, or is not valid, or in case of duplicates
    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER)")
    @PostMapping("/experts/createExpert")
    @ResponseStatus(HttpStatus.CREATED)
    fun createExpert(@RequestBody @Valid @NotNull expert: CreateExpertDTO) {
        expertService.createExpert(expert)
    }

}
