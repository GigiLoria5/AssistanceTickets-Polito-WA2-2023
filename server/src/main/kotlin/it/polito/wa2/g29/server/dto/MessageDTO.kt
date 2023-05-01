package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Attachment
import it.polito.wa2.g29.server.model.Message

data class MessageDTO(
    val messageId: Int?,
    val sender: String,
    val expertId: Int?,
    val content: String,
    val time: Long,
    val attachments: List<AttachmentDTO>
)

fun Message.toDTO(): MessageDTO {
    val attachmentDTOs = attachments.map { it.toDTO() }
    return MessageDTO(id, sender.toString(), expert?.id, content, time, attachmentDTOs)
}