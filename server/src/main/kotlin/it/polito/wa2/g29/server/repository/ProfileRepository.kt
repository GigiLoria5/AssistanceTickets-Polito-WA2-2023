package it.polito.wa2.g29.server.repository

import it.polito.wa2.g29.server.model.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface ProfileRepository : JpaRepository<Profile, Int> {
    @Transactional(readOnly = true)
    fun findProfileByEmail(email: String): Profile?

    @Transactional(readOnly = true)
    fun findProfileByPhoneNumber(phoneNumber: String): Profile?
}