package it.polito.wa2.g29.server.model

import it.polito.wa2.g29.server.enums.Expertise
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "experts")
class Expert {
    @Id
    var expertId: Int = 0

    var name: String = ""

    var surname: String = ""

    var email: String = ""

    // var expertises: Set<Expertise> = setOf()
}