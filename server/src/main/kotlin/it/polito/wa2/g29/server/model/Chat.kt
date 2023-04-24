package it.polito.wa2.g29.server.model

import jakarta.persistence.*

@Entity
@Table(name = "chats")
class Chat(
    @OneToOne
    @MapsId
    val ticket: Ticket
) : EntityBase<Long>() {
    @OneToMany(mappedBy = "chat")
    var messages: Set<Message> = mutableSetOf()
}