package io.hhplus.lectureapplyservice.interfaces.advice

import io.hhplus.lectureapplyservice.common.response.ErrorCode
import io.hhplus.lectureapplyservice.common.response.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String?>> {
        val errors =
            ex.bindingResult.allErrors.associate {
                (it as FieldError).field to it.defaultMessage
            }
        return ResponseEntity.badRequest().body(errors)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception): ResponseEntity<ErrorResponse<String?>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.INTERNAL_SERVER_ERROR)
        return ResponseEntity.internalServerError().body(errorResponse)
    }
}
