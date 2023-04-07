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

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Profile) return false
        return profileId == other.profileId && email == other.email && name == other.name
                && surname == other.surname && phoneNumber == other.phoneNumber && address == other.address
                && city == other.city && country == other.country
    }

    override fun hashCode(): Int {
        var result = profileId ?: 0
        result = 31 * result + email.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + surname.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + city.hashCode()
        result = 31 * result + country.hashCode()
        return result
    }
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