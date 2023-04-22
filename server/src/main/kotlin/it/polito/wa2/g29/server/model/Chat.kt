package it.polito.wa2.g29.server.model

import jakarta.persistence.*

@Entity
@Table(name = "chats")
class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var chatId: Int = 0

    @OneToOne
    @JoinColumn(name = "ticket_id")
    var ticket: Ticket? = null

    @OneToMany(mappedBy = "chat")
    var messages: Set<Message> = setOf()
}