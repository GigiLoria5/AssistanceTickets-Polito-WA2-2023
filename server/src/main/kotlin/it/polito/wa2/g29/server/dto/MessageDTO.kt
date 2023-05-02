package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Message

data class MessageDTO(
    val messageId: Int?,
    val sender: String,
    val expertId: Int?,
    val content: String,
    val attachments: List<AttachmentDTO>,
    val time: Long
)

fun Message.toDTO(): MessageDTO {
    val attachmentDTOs = attachments.map { it.toDTO() }
    return MessageDTO(id, sender.toString(), expert?.id, content, attachmentDTOs, time)
}