package it.polito.wa2.g29.server.model

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "experts")
class Expert(
    var name: String,
    var surname: String,
    var email: String,
    // var expertises: Set<Expertise> = setOf()
) : EntityBase<Long>()