package it.polito.wa2.g29.server.controller

import it.polito.wa2.g29.server.dto.ExpertDTO
import it.polito.wa2.g29.server.service.ExpertService
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
class ExpertController(private val expertService: ExpertService) {
    @GetMapping("/experts")
    fun getAllExperts(): List<ExpertDTO> {
        return expertService.getAllExperts()
    }

    @GetMapping("/experts/{expertId}")
    fun getExpertById(@PathVariable @Valid @Min(1) expertId: Int): ExpertDTO? {
        return expertService.getExpertById(expertId)
    }
}