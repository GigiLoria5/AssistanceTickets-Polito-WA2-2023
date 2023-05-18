package it.polito.wa2.g29.server.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.Nullable
import org.springframework.web.multipart.MultipartFile

data class NewMessageDTO(
    @field:NotBlank val content: String,
    @field:Nullable val attachments: List<@Valid MultipartFile>?
)

data class NewMessageIdDTO(
    val messageId: Int
)

