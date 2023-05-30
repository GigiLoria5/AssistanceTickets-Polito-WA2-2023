package it.polito.wa2.g29.server.controller

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g29.server.dto.ExpertDTO
import it.polito.wa2.g29.server.dto.TicketChangeDTO
import it.polito.wa2.g29.server.dto.TicketDTO
import it.polito.wa2.g29.server.service.ExpertService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.math.exp

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

    // GET /API/experts/{expertId}/tickets -- list all tickets assigned to an expert {expertId} or fail if it does not exist
    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER) or hasAuthority(@AuthUtil.ROLE_EXPERT)")
    @GetMapping("/experts/{expertId}/tickets")
    fun getAllTicketsByExpertId(@PathVariable @Valid @Min(1) expertId: Int): List<TicketDTO> {
        log.info("Retrieve all tickets assigned to {}", expertId)
        return expertService.getAllTicketsByExpertId(expertId)
    }

    // GET /API/experts/{expertId}/statusChanges -- details of tickets status changes done by an expert {expertId} or fail if it does not exist
    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_MANAGER) or hasAuthority(@AuthUtil.ROLE_EXPERT)")
    @GetMapping("/experts/{expertId}/statusChanges")
    fun getTicketStatusChangesByExpertId(@PathVariable @Valid @Min(1) expertId: Int): List<TicketChangeDTO> {
        log.info("Retrieve details of ticket status change done by expert: {}", expertId)
        return expertService.getTicketStatusChangesByExpertId(expertId)
    }

}
