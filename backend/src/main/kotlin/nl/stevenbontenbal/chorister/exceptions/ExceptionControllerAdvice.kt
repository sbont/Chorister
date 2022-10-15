package nl.stevenbontenbal.chorister.exceptions

import nl.stevenbontenbal.chorister.model.dto.ErrorMessage
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler
    fun handleInvalidInputException(ex: InvalidInputException): ResponseEntity<ErrorMessage> {
        val message = ErrorMessage(
            HttpStatus.BAD_REQUEST,
            ex.message
        )
        return message.toResponseEntity()
    }

    @ExceptionHandler
    fun handleAuthException(ex: AuthException): ResponseEntity<ErrorMessage> {
        val message = ErrorMessage(
            HttpStatus.UNAUTHORIZED,
            "There has been a server error during authentication: " + ex.message
        )
        return message.toResponseEntity()
    }
}