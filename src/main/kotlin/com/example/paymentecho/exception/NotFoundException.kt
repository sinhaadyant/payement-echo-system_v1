package com.example.paymentecho.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Domain-specific NotFound exception.
 * Use message codes (key) so the GlobalExceptionHandler can resolve localized messages.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(
    val code: String,
    val args: Array<Any> = emptyArray()
) : RuntimeException(code)
