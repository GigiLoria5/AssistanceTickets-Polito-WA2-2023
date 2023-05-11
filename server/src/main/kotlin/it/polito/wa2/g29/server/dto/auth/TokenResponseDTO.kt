package it.polito.wa2.g29.server.dto.auth

import com.nimbusds.jose.shaded.gson.annotations.SerializedName

data class TokenResponseDTO(
    @SerializedName("access_token") val accessToken: String
)

