package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.dto.NewMessageDTO
import it.polito.wa2.g29.server.enums.AttachmentType
import it.polito.wa2.g29.server.enums.UserType
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Message
import it.polito.wa2.g29.server.model.Ticket
import it.polito.wa2.g29.server.repository.MessageRepository
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.util.*

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

    fun getMessages(ticket: Ticket, expert: Expert): List<Message> {
        return listOf(
            Message(UserType.EXPERT, "Message1", ticket, expert),
            Message(UserType.CUSTOMER, "Message2", ticket, null),
            Message(UserType.EXPERT, "Message3", ticket, expert),
            Message(UserType.CUSTOMER, "Message4", ticket, null),
            Message(UserType.EXPERT, "Message5", ticket, expert),
            Message(UserType.CUSTOMER, "Message6", ticket, null)
        )
    }

    fun getNewMessageDTO(sender: UserType, content: String, attachmentType: AttachmentType?): NewMessageDTO {
        if (attachmentType == null)
            return NewMessageDTO(sender, content, null)

        val attachment = createAttachment(attachmentType)
        val attachments = listOf(attachment)
        return NewMessageDTO(sender, content, attachments)
    }

    fun createAttachment(attachmentType: AttachmentType): MultipartFile {
        val filename = UUID.randomUUID().toString()
        val contentType = when (attachmentType) {
            AttachmentType.PDF -> "application/pdf"
            AttachmentType.JPEG -> "image/jpeg"
            AttachmentType.PNG -> "image/png"
            AttachmentType.DOC -> "application/msword"
            else -> "application/octet-stream"
        }
        val content = byteArrayOf(0x00, 0x01, 0x02)
        return InMemoryMultipartFile(filename, filename, contentType, content)
    }

}

class InMemoryMultipartFile(
    private val name: String,
    private val originalFilename: String,
    private val contentType: String,
    private val content: ByteArray
) : MultipartFile {
    override fun getName(): String = name
    override fun getOriginalFilename(): String = originalFilename
    override fun getContentType(): String = contentType
    override fun isEmpty(): Boolean = content.isEmpty()
    override fun getSize(): Long = content.size.toLong()
    override fun getBytes(): ByteArray = content
    override fun getInputStream(): InputStream = ByteArrayInputStream(content)
    override fun transferTo(dest: File) {
        Files.write(dest.toPath(), content)
    }
}