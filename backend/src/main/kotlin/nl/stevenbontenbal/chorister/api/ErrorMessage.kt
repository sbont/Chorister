package nl.stevenbontenbal.chorister.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class ErrorMessage(
    val status: HttpStatus,
    val message: String?
) {
    fun toResponseEntity(): ResponseEntity<ErrorMessage> {
        return ResponseEntity(this, status)
    }
}