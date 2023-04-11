package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.dto.ProfileDTO
import jakarta.persistence.*

@Entity
@Table(name = "profiles")
class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    var profileId: Int? = null

    var email: String = ""

    var name: String = ""

    var surname: String = ""

    @Column(name = "phone_number")
    var phoneNumber: String = ""

    var address: String = ""

    var city: String = ""

    var country: String = ""
}

fun ProfileDTO.toEntity(): Profile {
    val profile = Profile()
    profile.profileId = this.profileId
    profile.email = this.email
    profile.name = this.name
    profile.surname = this.surname
    profile.phoneNumber = this.phoneNumber
    profile.address = this.address
    profile.city = this.city
    profile.country = this.country
    return profile
}