package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.enums.UserType
import jakarta.validation.Valid
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.springframework.web.multipart.MultipartFile

data class NewMessageDTO(
    @field:NotNull @Valid val sender: UserType,
    @field:NotNull val content: String,
    @field:Nullable val attachments: List<@Valid MultipartFile>?
)

data class NewMessageIdDTO(
    val messageId: Int
)

