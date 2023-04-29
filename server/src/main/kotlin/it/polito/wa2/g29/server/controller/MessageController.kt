package it.polito.wa2.g29.server.controller

import it.polito.wa2.g29.server.dto.MessageDTO
import it.polito.wa2.g29.server.service.MessageService
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
class MessageController(private val messageService: MessageService) {
    @GetMapping("/chats/{ticketId}/messages")
    fun getMessagesByTicketId(@PathVariable @Min(1) @Valid ticketId: Int): List<MessageDTO> {
        return messageService.getMessagesByTicketId(ticketId)
    }
}