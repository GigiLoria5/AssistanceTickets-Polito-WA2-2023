package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.enums.AttachmentType
import jakarta.persistence.*

@Entity
@Table(name = "attachments")
class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var attachmentId: Int = 0

    var name: String = ""

    var file: Array<Byte> = arrayOf()

    var type: AttachmentType? = null

    @ManyToOne
    @JoinColumn(name = "message_id")
    var message: Message? = null
}
