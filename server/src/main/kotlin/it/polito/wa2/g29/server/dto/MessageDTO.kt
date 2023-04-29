package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.model.Message

data class MessageDTO(
    val messageId: Int?,
    val sender: String,
    val expertId: Int?,
    val content: String,
    val time: Long
)

fun Message.toDTO(): MessageDTO {
    return MessageDTO(id, sender.toString(), expert?.id, content, time)
}