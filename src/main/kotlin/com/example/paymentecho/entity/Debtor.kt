package com.example.paymentecho.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

/**
 * Minimal Debtor entity for outgoing payment counterparties.
 */
@Entity
@Table(name = "debtors")
data class Debtor(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val name: String = "Unknown",

    @Column(nullable = true)
    val metadata: String? = null
)
