package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.enums.AttachmentType
import jakarta.persistence.*

@Entity
@Table(
    name = "attachments"
)
class Attachment(
    var name: String,
    @Column(nullable = false)
    var file: ByteArray,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var type: AttachmentType,
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var message: Message
) : EntityBase<Int>()
