package com.example.paymentecho.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.util.*

/**
 * DTO used for upstream (client) requests to create/pay/echo payments.
 *
 * - Keep DTOs immutable (data class) for thread-safety and easy serialization.
 * - Use Jakarta validation annotations so controllers can validate at the boundary.
 */
data class PaymentRequest(
    @field:NotNull(message = "{payment.amount.notnull}")
    @field:DecimalMin(value = "0.01", message = "{payment.amount.min}")
    val amount: Double? = null,

    @field:NotBlank(message = "{payment.currency.notblank}")
    val currency: String = "USD",

    val status: String? = "RECEIVED",

    // Optional: allow clients to provide an external reference
    val externalReference: String? = null
)
