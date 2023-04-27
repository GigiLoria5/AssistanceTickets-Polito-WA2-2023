package it.polito.wa2.g29.server.advice

import it.polito.wa2.g29.server.exception.DuplicateProfileException
import it.polito.wa2.g29.server.exception.ExpertNotFoundException
import it.polito.wa2.g29.server.exception.ProductNotFoundException
import it.polito.wa2.g29.server.exception.ProfileNotFoundException
import it.polito.wa2.g29.server.exception.TicketNotFoundException
import it.polito.wa2.g29.server.utils.ErrorMessage
import jakarta.validation.ConstraintViolationException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import kotlin.Exception

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class ApplicationExceptionHandler {
    // 404 - Not Found
    @ExceptionHandler(value = [ProductNotFoundException::class, ProfileNotFoundException::class, ExpertNotFoundException::class, TicketNotFoundException::class])
    fun handleNotFoundException(exception: Exception): ResponseEntity<Unit> {
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    // 409 - Conflict
    @ExceptionHandler(value = [DuplicateProfileException::class])
    fun handleDuplicateException(exception: Exception): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(exception.message.orEmpty())
        return ResponseEntity(errorMessage, HttpStatus.CONFLICT)
    }

    /*
     * ConstraintViolationException         from @Validated
     * MethodArgumentNotValidException      from @Valid
     * HttpMessageNotReadableException      from missing field
     * MethodArgumentTypeMismatchException  from failing type coercion
     *
     * 422 - Generic Message
     */
    @ExceptionHandler(value = [ConstraintViolationException::class, MethodArgumentNotValidException::class, HttpMessageNotReadableException::class, MethodArgumentTypeMismatchException::class])
    fun handleValidationFailedException(exception: Exception): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage("validation of request failed")
        return ResponseEntity(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    // 500 - Generic Error
    @ExceptionHandler(Exception::class)
    fun handleGenericException(exception: Exception): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(exception.message.orEmpty())
        return ResponseEntity(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}