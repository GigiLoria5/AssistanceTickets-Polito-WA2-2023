package it.polito.wa2.g29.server.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.util.*

@Entity
@Table(name = "product_tokens")
class ProductToken(
    @OneToOne
    var product: Product
) : EntityBase<Int>() {
    @OneToOne
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
