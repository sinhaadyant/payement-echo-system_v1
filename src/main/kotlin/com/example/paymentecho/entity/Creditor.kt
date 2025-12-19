package com.example.paymentecho.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

/**
 * Minimal Creditor entity. In production you would include address, KYC refs, identifiers, etc.
 * This is intentionally small and focused; expand fields as required by business rules.
 */
@Entity
@Table(name = "creditors")
data class Creditor(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val name: String = "Unknown",

    // TODO: add additional metadata like contact info, routing number, etc.
    @Column(nullable = true)
    val metadata: String? = null
)
