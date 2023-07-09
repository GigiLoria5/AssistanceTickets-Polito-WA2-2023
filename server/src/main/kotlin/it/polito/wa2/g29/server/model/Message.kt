package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.enums.UserType
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate

@Entity
@Table(
    name = "messages",
    uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("sender", "ticket_id", "time"))]
)
class Message(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var sender: UserType,
    @Column(nullable = false)
    var content: String,
    @ManyToOne
    var ticket: Ticket,
    @ManyToOne
    var expert: Expert?
) : EntityBase<Int>() {
    @OneToMany(mappedBy = "message", cascade = [CascadeType.ALL])
    var attachments: List<Attachment> = mutableListOf()

    @CreatedDate
    @Column(updatable = false, nullable = false)
    var time: Long = System.currentTimeMillis()
}
