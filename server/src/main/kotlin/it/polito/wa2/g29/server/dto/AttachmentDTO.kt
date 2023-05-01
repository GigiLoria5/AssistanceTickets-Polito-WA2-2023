package it.polito.wa2.g29.server.dto

import it.polito.wa2.g29.server.enums.AttachmentType
import it.polito.wa2.g29.server.model.Attachment

data class AttachmentDTO(
    val attachmentId: Int?,
    val name: String?,
    val type: String
)

fun Attachment.toDTO(): AttachmentDTO {
    return AttachmentDTO(id, name, type.toString())
}

data class FileAttachmentDTO(
    val name: String,
    val type: AttachmentType,
    val file: ByteArray
)