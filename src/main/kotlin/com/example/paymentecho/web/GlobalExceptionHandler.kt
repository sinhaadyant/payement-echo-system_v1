package com.example.paymentecho.web

import com.example.paymentecho.exception.BadRequestException
import com.example.paymentecho.exception.NotFoundException
import jakarta.validation.ConstraintViolationException
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*

/**
 * Centralized exception handling:
 * - Maps known domain exceptions to structured JSON errors
 * - Resolves message codes using MessageSource for i18n
 * - Catches validation exceptions and returns detailed field errors
 */
@ControllerAdvice
class GlobalExceptionHandler(private val messages: MessageSource) {

    private fun msg(code: String, locale: Locale = Locale.getDefault(), args: Array<Any> = emptyArray()): String? =
        messages.getMessage(code, args, code, locale)

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException, locale: Locale): ResponseEntity<Any> {
        val message = msg(ex.code, locale, ex.args)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to message))
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException, locale: Locale): ResponseEntity<Any> {
        val message = msg(ex.code, locale, ex.args)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, locale: Locale): ResponseEntity<Any> {
        val errors = ex.bindingResult.fieldErrors.associate { fe: FieldError -> fe.field to (fe.defaultMessage ?: "invalid") }
        val message = msg("validation.failed", locale)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to message, "errors" to errors))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException, locale: Locale): ResponseEntity<Any> {
        val errors = ex.constraintViolations.associate { it.propertyPath.toString() to it.message }
        val message = msg("validation.failed", locale)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to message, "errors" to errors))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception, locale: Locale): ResponseEntity<Any> {
        // Production: log stack traces to structured logs and a monitoring pipeline (Sentry, DataDog, etc.)
        ex.printStackTrace()
        val message = msg("internal.error", locale)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to message))
    }
}
