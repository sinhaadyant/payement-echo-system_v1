package com.example.paymentecho.service

import com.example.paymentecho.entity.Debtor
import com.example.paymentecho.repository.DebtorRepository
import org.springframework.stereotype.Service
import java.util.*

/**
 * Debtor service stub.
 * Mirror the responsibilities of CreditorService — separated for SRP and future divergence.
 */
@Service
class DebtorService(private val repo: DebtorRepository) {

    fun findById(id: UUID): Debtor? = repo.findById(id).orElse(null)
    fun create(debtor: Debtor): Debtor = repo.save(debtor)

    // TODO: add business rules for debtor risk checks
}
