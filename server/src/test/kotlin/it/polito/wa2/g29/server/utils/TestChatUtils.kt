package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Message
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.MessageRepository

object TestChatUtils {

    fun addMessages(messageRepository: MessageRepository, messages: List<Message>, ticket: Ticket, expert: Expert) {
        messages.forEach {
            expert.addMessage(it)
            ticket.addMessage(it)
            it.time = System.currentTimeMillis()
            messageRepository.save(it)
        }
    }

    fun deleteAllMessages(messageRepository: MessageRepository, tickets: List<Ticket>, experts: List<Expert>) {
        tickets.forEach {
            it.messages = mutableSetOf()
        }
        experts.forEach {
            it.messages = mutableSetOf()
        }
        messageRepository.deleteAllInBatch()
    }

    fun createMessages(ticket: Ticket, expert: Expert): List<Message> {
        return listOf(
            Message(UserType.EXPERT, "Message1", ticket, expert),
            Message(UserType.CUSTOMER, "Message2", ticket, expert),
            Message(UserType.EXPERT, "Message3", ticket, expert),
            Message(UserType.CUSTOMER, "Message4", ticket, expert),
            Message(UserType.EXPERT, "Message5", ticket, expert),
            Message(UserType.CUSTOMER, "Message6", ticket, expert)
        )
    }

}