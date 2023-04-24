package it.polito.wa2.g29.server.model

import jakarta.persistence.*

@Entity
@Table(name = "products")
class Product(
    var asin: String,
    var brand: String,
    var category: String,
    @Column(name = "manufacturer_number")
    var manufacturerNumber: String,
    var name: String,
    var price: Float,
    var weight: Float,
) : EntityBase<Long>()