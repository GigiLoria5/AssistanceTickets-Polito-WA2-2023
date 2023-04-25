package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.dto.ProfileDTO
import jakarta.persistence.*

@Entity
@Table(name = "profiles")
class Profile(
    @Column(nullable = false, unique = true)
    var email: String,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var surname: String,
    @Column(name = "phone_number", nullable = false)
    var phoneNumber: String,
    @Column(nullable = false)
    var address: String,
    @Column(nullable = false)
    var city: String,
    @Column(nullable = false)
    var country: String
) : EntityBase<Int>() {

    @OneToMany(mappedBy = "customer")
    var tickets: MutableSet<Ticket> = mutableSetOf()

    fun update(newProfile: ProfileDTO) {
        email = newProfile.email
        name = newProfile.name
        surname = newProfile.surname
        phoneNumber = newProfile.phoneNumber
        address = newProfile.address
        city = newProfile.city
        country = newProfile.country
    }

}

fun ProfileDTO.toEntity(): Profile {
    return Profile(email, name, surname, phoneNumber, address, city, country)
}

