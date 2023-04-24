package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.enums.UserType
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate


@Table
@Entity(name = "messages")
class Message(
    var sender: UserType,
    var content: String,
    @ManyToOne
    var chat: Chat,
    @ManyToOne
    var expert: Expert?
) : EntityBase<Int>() {
    @OneToMany(mappedBy = "message")
    var attachments: Set<Attachment> = mutableSetOf()

    @CreatedDate
    @Column(updatable = false, nullable = false)
    var time: Long = System.currentTimeMillis()
}
