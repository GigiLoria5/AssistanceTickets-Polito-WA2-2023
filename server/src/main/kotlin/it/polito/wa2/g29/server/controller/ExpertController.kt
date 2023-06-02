package it.polito.wa2.g29.server.controller

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g29.server.dto.CreateExpertDTO
import it.polito.wa2.g29.server.dto.ExpertDTO
import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.dto.TicketDTO
import it.polito.wa2.g29.server.service.ExpertService
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
class ExpertController(private val expertService: ExpertService) {

    private val log = LoggerFactory.getLogger(ExpertController::class.java)

    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER)")
    @GetMapping("/experts")
    fun getAllExperts(): List<ExpertDTO> {
        log.info("Retrieve all experts")
        return expertService.getAllExperts()
    }

    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER) or hasAuthority(@AuthUtil.ROLE_EXPERT)")
    @GetMapping("/experts/{expertId}")
    fun getExpertById(@PathVariable @Valid @Min(1) expertId: Int): ExpertDTO? {
        log.info("Retrieve expert: {}", expertId)
        return expertService.getExpertById(expertId)
    }

    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER) or hasAuthority(@AuthUtil.ROLE_EXPERT)")
    @GetMapping("/experts/{expertId}/tickets")
    fun getAllTicketsByExpertId(@PathVariable @Valid @Min(1) expertId: Int): List<TicketDTO> {
        log.info("Retrieve all tickets assigned to {}", expertId)
        return expertService.getAllTicketsByExpertId(expertId)
    }

    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER) or hasAuthority(@AuthUtil.ROLE_EXPERT)")
    @GetMapping("/experts/{expertId}/statusChanges")
    fun getTicketStatusChangesByExpertId(@PathVariable @Valid @Min(1) expertId: Int): List<TicketChangeDTO> {
        log.info("Retrieve details of ticket status change done by expert: {}", expertId)
        return expertService.getTicketStatusChangesByExpertId(expertId)
    }

    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER)")
    @PostMapping("/experts/createExpert")
    @ResponseStatus(HttpStatus.CREATED)
    fun createExpert(@RequestBody @Valid @NotNull expert: CreateExpertDTO) {
        expertService.createExpert(expert)
    }

}
