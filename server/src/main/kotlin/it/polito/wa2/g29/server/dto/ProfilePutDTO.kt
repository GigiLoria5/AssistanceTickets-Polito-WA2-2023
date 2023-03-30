package it.polito.wa2.g29.server.dto

data class ProfilePutDTO(
    val name: String?,
    val surname: String?,
    val phoneNumber: String?,
    val address: String?,
    val city: String?,
    val country: String?
)

fun ProfilePutDTO.allFieldsAreNullOrBlank(): Boolean{
    if (!name.isNullOrBlank())
        return false
    if (!surname.isNullOrBlank())
        return false
    if (!phoneNumber.isNullOrBlank())
        return false
    if (!address.isNullOrBlank())
        return false
    if (!city.isNullOrBlank())
        return false
    if (!country.isNullOrBlank())
        return false
    return true

}