package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.dto.ProfileDTO
import jakarta.persistence.*

@Entity
@Table(name = "profiles")
class Profile(
    var email: String,
    var name: String,
    var surname: String,
    @Column(name = "phone_number")
    var phoneNumber: String,
    var address: String,
    var city: String,
    var country: String
) : EntityBase<Long>()

fun ProfileDTO.toEntity(): Profile {
    return Profile(email, name, surname, phoneNumber, address, city, country)
}