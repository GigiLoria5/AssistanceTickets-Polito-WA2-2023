package it.polito.wa2.g29.server.model

import jakarta.persistence.*

@Entity
@Table(name = "products")
class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    var productId: Int = 0

    var asin: String = ""

    var brand: String = ""

    var category: String = ""

    @Column(name = "manufacturer_number")
    var manufacturerNumber: String = ""

    var name: String = ""

    var price: Float = 0.0F

    var weight: Float = 0.0F
}