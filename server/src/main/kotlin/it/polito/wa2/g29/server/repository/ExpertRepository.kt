package it.polito.wa2.g29.server.repository

import it.polito.wa2.g29.server.model.Expert
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExpertRepository: JpaRepository<Expert,Int> {
}