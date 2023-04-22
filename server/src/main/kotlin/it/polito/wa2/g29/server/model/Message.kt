package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.enums.UserType
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate


@Table
@Entity(name = "messages")
class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var messageId: Int = 0

    @ManyToOne
    @JoinColumn(name = "chat_id")
    var chat: Chat? = null

    var sender: UserType? = null

    @ManyToOne
    @JoinColumn(name = "expert_id")
    var expert: Expert? = null

    var content: String = ""

    @OneToMany(mappedBy = "message")
    var attachments: Set<Attachment>? = null

    @CreatedDate
    var time: Long = 0
}
