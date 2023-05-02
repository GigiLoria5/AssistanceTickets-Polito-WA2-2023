package it.polito.wa2.g29.server.controller

import it.polito.wa2.g29.server.dto.MessageDTO
import it.polito.wa2.g29.server.dto.NewMessageDTO
import it.polito.wa2.g29.server.dto.NewMessageIdDTO
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.service.ChatService
import it.polito.wa2.g29.server.utils.MediaTypeUtil
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/API")
@Validated
@RestController
class ChatController(private val chatService: ChatService) {
    @GetMapping("/chats/{ticketId}/messages")
    fun getMessagesByTicketId(@PathVariable @Min(1) @Valid ticketId: Int): List<MessageDTO> {
        return chatService.getMessagesByTicketId(ticketId)
    }

    @PostMapping("/chats/{ticketId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    fun addMessageWithAttachments(
        @PathVariable @Min(1) @Valid ticketId: Int,
        @RequestParam("sender") sender: UserType,
        @RequestParam("content") content: String,
        @RequestPart("attachments") attachments: List<MultipartFile>?
    ): NewMessageIdDTO {
        val newMessage = NewMessageDTO(sender, content, attachments)
        return chatService.addMessageWithAttachments(ticketId, newMessage)
    }

    @GetMapping("/chats/{ticketId}/messages/{messageId}/attachments/{attachmentId}")
    fun getAttachments(
        @PathVariable @Min(1) @Valid ticketId: Int,
        @PathVariable @Min(1) @Valid messageId: Int,
        @PathVariable @Min(1) @Valid attachmentId: Int

    ): ResponseEntity<ByteArray> {
        val attachment = chatService.getAttachments(ticketId, messageId, attachmentId)
        val mediaType = MediaTypeUtil.attachmentTypeToMediaType(attachment.type)
        return ResponseEntity
            .ok()
            .contentType(mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${attachment.name}\"")
            .body(attachment.file)
    }
}