package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.dto.auth.CreateClientDTO
import it.polito.wa2.g29.server.dto.profile.EditProfileDTO
import jakarta.persistence.*

@Entity
@Table(name = "profiles")
class Profile(
    @Column(nullable = false, unique = true, updatable = false)
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

    @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL])
    var tickets: MutableSet<Ticket> = mutableSetOf()

    fun update(newProfile: EditProfileDTO) {
        name = newProfile.name
        surname = newProfile.surname
        phoneNumber = newProfile.phoneNumber
        address = newProfile.address
        city = newProfile.city
        country = newProfile.country
    }

}

fun CreateClientDTO.toEntity(): Profile {
    return Profile(email, name, surname, phoneNumber, address, city, country)
}

