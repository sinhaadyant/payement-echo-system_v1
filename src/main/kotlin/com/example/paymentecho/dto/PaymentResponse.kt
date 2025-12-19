package com.example.paymentecho.dto

import java.time.Instant
import java.util.*

/**
 * Downstream DTO returned to clients. Values are final and read-only.
 * Keeps internal entity details (like JPA specifics) out of the outward contract.
 */
data class PaymentResponse(
    val id: UUID,
    val amount: Double,
    val currency: String,
    val status: String,
    val createdAt: Instant,
    val externalReference: String? = null
)
