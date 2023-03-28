package it.polito.wa2.g29.server.advice

import it.polito.wa2.g29.server.exception.ProductNotFoundException
import it.polito.wa2.g29.server.utils.ErrorMessage
import jakarta.validation.ConstraintViolationException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import kotlin.Exception

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class ApplicationExceptionHandler {
    // 404 - Not Found
    @ExceptionHandler(value = [ProductNotFoundException::class])
    fun handleNotFoundException(exception: Exception): ResponseEntity<Unit> {
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    /*
     * ConstraintViolationException         from @Validated
     * MethodArgumentNotValidException      from @Valid
     * MethodArgumentTypeMismatchException  from failing type coercion
     *
     * 422 - Generic Message
     */
    @ExceptionHandler(value = [ConstraintViolationException::class, MethodArgumentNotValidException::class, MethodArgumentTypeMismatchException::class])
    fun handleValidationFailedException(exception: Exception): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage("validation of request failed")
        return ResponseEntity(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    // 500 - Generic Error
    @ExceptionHandler(Exception::class)
    fun handleGenericException(exception: Exception): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage("an error occur, please retry")
        return ResponseEntity(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}