package it.polito.wa2.g29.server.controller

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g29.server.dto.MessageDTO
import it.polito.wa2.g29.server.dto.NewMessageDTO
import it.polito.wa2.g29.server.dto.NewMessageIdDTO
import it.polito.wa2.g29.server.service.ChatService
import it.polito.wa2.g29.server.utils.MediaTypeUtil
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/API")
@Validated
@RestController
@Observed
class ChatController(private val chatService: ChatService) {

    private val log = LoggerFactory.getLogger(ChatController::class.java)

    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_CLIENT) or hasAuthority(@AuthUtil.ROLE_EXPERT)")
    @GetMapping("/chats/{ticketId}/messages")
    fun getMessagesByTicketId(@PathVariable @Min(1) @Valid ticketId: Int): List<MessageDTO> {
        log.info("Retrieve all messages for ticket: {}", ticketId)
        return chatService.getMessagesByTicketId(ticketId)
    }

    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_CLIENT) or hasAuthority(@AuthUtil.ROLE_EXPERT)")
    @PostMapping("/chats/{ticketId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    fun addMessageWithAttachments(
        @PathVariable @Min(1) @Valid ticketId: Int,
        @RequestParam("content") @NotBlank content: String,
        @RequestPart("attachments") attachments: List<MultipartFile>?
    ): NewMessageIdDTO {
        val newMessage = NewMessageDTO(content, attachments)
        log.info("Send message for ticket: {}", ticketId)
        return chatService.addMessageWithAttachments(ticketId, newMessage)
    }

    @PreAuthorize("hasAuthority(@AuthUtil.ROLE_CLIENT) or hasAuthority(@AuthUtil.ROLE_EXPERT)")
    @GetMapping("/chats/{ticketId}/messages/{messageId}/attachments/{attachmentId}")
    fun getAttachments(
        @PathVariable @Min(1) @Valid ticketId: Int,
        @PathVariable @Min(1) @Valid messageId: Int,
        @PathVariable @Min(1) @Valid attachmentId: Int
    ): ResponseEntity<ByteArray> {
        val attachment = chatService.getAttachment(ticketId, messageId, attachmentId)
        val mediaType = MediaTypeUtil.attachmentTypeToMediaType(attachment.type)
        log.info("Retrieve attachment: {} of message: {} of ticket: {}",attachmentId, messageId,ticketId)
        return ResponseEntity
            .ok()
            .contentType(mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${attachment.name}\"")
            .body(attachment.file)
    }

}
