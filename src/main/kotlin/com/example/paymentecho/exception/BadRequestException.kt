package com.example.paymentecho.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException(
    val code: String,
    val args: Array<Any> = emptyArray()
) : RuntimeException(code)
