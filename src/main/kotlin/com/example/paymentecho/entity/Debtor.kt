package com.example.paymentecho.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.*

/**
 * Debtor entity for outgoing payment counterparties.
 */
@Entity
@Table(name = "debtors")
data class Debtor(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val name: String = "Unknown",

    @Column(nullable = false, name = "account_number")
    val accountNumber: String,

    @Column(nullable = false, name = "bank_code")
    val bankCode: String,

    @Column(nullable = true)
    val address: String? = null,

    @Column(nullable = true)
    val email: String? = null,

    @Column(nullable = false, name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false, name = "updated_at")
    val updatedAt: Instant = Instant.now(),

    @Column(nullable = true)
    val metadata: String? = null
)
