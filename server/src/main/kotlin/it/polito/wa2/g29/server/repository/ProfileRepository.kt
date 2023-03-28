package it.polito.wa2.g29.server.repository

import it.polito.wa2.g29.server.model.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfileRepository: JpaRepository<Profile, Int> {

    fun findProfileByEmail(email: String): Profile?
}