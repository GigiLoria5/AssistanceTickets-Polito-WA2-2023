package it.polito.wa2.g29.server.enums

enum class AttachmentType {
    JPEG,
    PNG,
    PDF,
    DOC,
    OTHER;

    companion object {
        fun fromMimeType(mimeType: String): AttachmentType {
            return when (mimeType) {
                "application/pdf" -> PDF
                "image/jpeg", "image/jpg" -> JPEG
                "image/png" -> PNG
                "application/msword" -> DOC
                else -> OTHER
            }
        }
    }
}