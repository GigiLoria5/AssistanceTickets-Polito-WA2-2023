package it.polito.wa2.g29.server.service

import it.polito.wa2.g29.server.dto.auth.AccessTokenRequestDTO
import it.polito.wa2.g29.server.dto.auth.CreateClientDTO
import it.polito.wa2.g29.server.dto.auth.TokenResponseDTO

interface AuthService {
    fun authenticateUser(request: AccessTokenRequestDTO): TokenResponseDTO

    fun addClient(createClientDTO: CreateClientDTO)
}
