package it.polito.wa2.g29.server.model

import jakarta.persistence.*

@Entity
@Table(name = "products")
class Product(
    @Column(nullable = false, unique = true)
    var asin: String,
    @Column(nullable = false)
    var brand: String,
    @Column(nullable = false)
    var category: String,
    @Column(name = "manufacturer_number", nullable = false)
    var manufacturerNumber: String,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var price: Float,
    @Column(nullable = false)
    var weight: Float,
) : EntityBase<Int>()