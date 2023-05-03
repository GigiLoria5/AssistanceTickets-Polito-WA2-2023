package it.polito.wa2.g29.server.unit.util

import it.polito.wa2.g29.server.enums.AttachmentType
import it.polito.wa2.g29.server.utils.MediaTypeUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class MediaTypeUtilUnitTest {

    @Test
    fun `attachmentTypeToMediaType should return correct MediaType for each AttachmentType`() {
        assertEquals(MediaType.IMAGE_JPEG, MediaTypeUtil.attachmentTypeToMediaType(AttachmentType.JPEG))
        assertEquals(MediaType.IMAGE_PNG, MediaTypeUtil.attachmentTypeToMediaType(AttachmentType.PNG))
        assertEquals(MediaType.APPLICATION_PDF, MediaTypeUtil.attachmentTypeToMediaType(AttachmentType.PDF))
        assertEquals(MediaType("application", "msword"), MediaTypeUtil.attachmentTypeToMediaType(AttachmentType.DOC))
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, MediaTypeUtil.attachmentTypeToMediaType(AttachmentType.OTHER))
    }

}