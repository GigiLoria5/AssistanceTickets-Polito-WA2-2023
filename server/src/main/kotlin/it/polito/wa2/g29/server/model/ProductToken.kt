package it.polito.wa2.g29.server.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "product_tokens")
class ProductToken(
    @OneToOne
    var product: Product
) : EntityBase<Int>() {
    @ManyToOne
    @JoinColumn(updatable = true, nullable = true)
    var user: Profile? = null

    @Column(nullable = false, unique = true)
    var token: String = ""

    @CreatedDate
    @Column(updatable = false, nullable = false, name = "created_at")
    var createdAt: Long = 0

    @LastModifiedDate
    var registeredAt: Long = 0

    @PrePersist
    fun generateToken() {
        this.token = UUID.randomUUID().toString()
    }
}
