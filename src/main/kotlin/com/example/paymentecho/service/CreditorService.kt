package com.example.paymentecho.service

import com.example.paymentecho.entity.Creditor
import com.example.paymentecho.repository.CreditorRepository
import org.springframework.stereotype.Service
import java.util.*

/**
 * Creditor service. For now this is a lightweight stub.
 *
 * TODO:
 *  - Add caching (Caffeine/Redis) for creditor lookups
 *  - Add KYC/verification calls
 *  - Add transactional boundaries and isolation levels if updates are required
 */
@Service
class CreditorService(private val repo: CreditorRepository) {

    fun findById(id: UUID): Creditor? = repo.findById(id).orElse(null)

    fun create(creditor: Creditor): Creditor = repo.save(creditor)

    // TODO: add update/delete operations with appropriate safeguards
}
