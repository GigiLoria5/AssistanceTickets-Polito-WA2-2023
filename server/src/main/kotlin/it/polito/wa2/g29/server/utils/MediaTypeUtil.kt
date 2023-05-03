package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.enums.AttachmentType
import org.springframework.http.MediaType

object MediaTypeUtil {
    fun attachmentTypeToMediaType(attachmentType: AttachmentType): MediaType {
        return when (attachmentType) {
            AttachmentType.JPEG -> MediaType.IMAGE_JPEG
            AttachmentType.PNG -> MediaType.IMAGE_PNG
            AttachmentType.PDF -> MediaType.APPLICATION_PDF
            AttachmentType.DOC -> MediaType("application", "msword")
            AttachmentType.OTHER -> MediaType.APPLICATION_OCTET_STREAM
        }
    }
}