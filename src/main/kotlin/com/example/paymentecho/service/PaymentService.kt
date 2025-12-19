package com.example.paymentecho.service

import com.example.paymentecho.dto.PaymentRequest
import com.example.paymentecho.dto.PaymentResponse
import com.example.paymentecho.exception.NotFoundException
import com.example.paymentecho.mapper.PaymentMapper
import com.example.paymentecho.repository.PaymentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Business service for payments.
 *
 * Responsibilities:
 *  - Accept DTOs from controllers (boundary)
 *  - Apply business logic and orchestrate repositories/other services (creditor/debtor)
 *  - Return downstream DTOs to controllers
 *
 * Transactions:
 *  - Use @Transactional for write operations.
 *  - For complex flows involving external systems, consider Saga/Outbox patterns.
 */
@Service
class PaymentService(
    private val repo: PaymentRepository,
    private val creditorService: CreditorService, // injected so we can extend behavior later
    private val debtorService: DebtorService
) {

    private val logger = LoggerFactory.getLogger(PaymentService::class.java)

    fun findAll(): List<PaymentResponse> =
        repo.findAll().map { PaymentMapper.toResponse(it) }

    fun findById(id: UUID): PaymentResponse =
        repo.findById(id)
            .map { PaymentMapper.toResponse(it) }
            .orElseThrow { NotFoundException("payment.notfound", arrayOf(id.toString())) }

    /**
     * Creates a new payment based on the incoming DTO.
     *
     * Logging:
     *  - INFO logs for business-level actions (create, update, delete)
     *  - DEBUG logs for internal details (save results, generated IDs)
     *
     * TODO:
     *  - Integrate creditor/debtor validation
     *  - Add fraud rules, velocity checks, compliance validations
     *  - Move echo semantics to workflow engine if needed
     */
    @Transactional
    fun create(request: PaymentRequest): PaymentResponse {
        logger.info(
            "Creating payment: amount={} currency={} externalReference={}",
            request.amount, request.currency, request.externalReference
        )

        // Convert upstream DTO → internal domain entity
        val entity = PaymentMapper.toEntity(request)

        // TODO: validate creditor, debtor, AML/KYC rules
        // creditorService.findById(...)
        // debtorService.findById(...)

        val saved = repo.save(entity)

        logger.debug("Payment saved successfully with id={}", saved.id)

        return PaymentMapper.toResponse(saved)
    }

    /**
     * Echo semantics: persist the request as a new payment and return the stored entity.
     * For high-volume systems, the echo flow might become a dedicated workflow.
     */
    @Transactional
    fun echo(request: PaymentRequest): PaymentResponse {
        logger.info("Echoing payment (creating duplicate record)")
        return create(request)
    }
}
