package it.polito.wa2.g29.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "profiles")
class Profile {
    @Id
    @Column(name = "profile_id")
    var profileId: Int = 0

    var email: String = ""

    var name: String = ""

    var surname: String = ""

    @Column(name = "phone_number")
    var phoneNumber: String = ""

    var address: String = ""

    var city: String = ""

    var country: String = ""
}