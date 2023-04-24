package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.enums.AttachmentType
import jakarta.persistence.*

@Entity
@Table(name = "attachments")
class Attachment(
    var name: String,
    var file: Array<Byte>,
    var type: AttachmentType,
    @ManyToOne
    var message: Message
) : EntityBase<Int>()
