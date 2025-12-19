package com.example.paymentecho.service

import com.example.paymentecho.dto.PaymentRequest
import com.example.paymentecho.dto.PaymentResponse
import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.dto.request.PaymentEchoRequest
import com.example.paymentecho.dto.response.PaymentResponse as ResponsePaymentResponse
import com.example.paymentecho.exception.NotFoundException
import com.example.paymentecho.mapper.PaymentMapper
import com.example.paymentecho.repository.PaymentRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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
    private val debtorService: DebtorService,
    private val mapper: PaymentMapper
) {

    private val logger = LoggerFactory.getLogger(PaymentService::class.java)

    fun findAll(): List<PaymentResponse> =
        repo.findAll().map { mapper.toResponse(it) }

    fun findAll(page: Int, size: Int, sort: String?, status: String?, currency: String?, minAmount: Double?, maxAmount: Double?): Page<ResponsePaymentResponse> {
        val sortObj = if (sort != null) {
            val parts = sort.split(",")
            if (parts.size == 2) {
                Sort.by(if (parts[1].lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC, parts[0])
            } else {
                Sort.by(Sort.Direction.DESC, "createdAt")
            }
        } else {
            Sort.by(Sort.Direction.DESC, "createdAt")
        }
        val pageable: Pageable = PageRequest.of(page, size, sortObj)
        val entityPage = repo.findAll(pageable)
        
        // Filter by status and currency if provided
        val filtered = entityPage.content.filter { payment ->
            (status == null || payment.status == status) &&
            (currency == null || payment.currency == currency) &&
            (minAmount == null || payment.amount >= minAmount) &&
            (maxAmount == null || payment.amount <= maxAmount)
        }
        
        // Create a custom page with filtered results
        val filteredPage = PageRequest.of(page, size, sortObj)
        val start = (page * size).toLong()
        val end = minOf((start + size), filtered.size.toLong())
        val pagedContent = filtered.subList(start.toInt(), end.toInt())
        
        return org.springframework.data.domain.PageImpl(
            pagedContent.map { mapper.toResponseResponse(it) },
            filteredPage,
            filtered.size.toLong()
        )
    }

    fun findById(id: UUID): PaymentResponse =
        repo.findById(id)
            .map { mapper.toResponse(it) }
            .orElseThrow { NotFoundException("payment.notfound", arrayOf(id.toString())) }

    fun findByIdResponse(id: UUID): ResponsePaymentResponse =
        repo.findById(id)
            .map { mapper.toResponseResponse(it) }
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
        val entity = mapper.toEntity(request)

        // TODO: validate creditor, debtor, AML/KYC rules
        // creditorService.findById(...)
        // debtorService.findById(...)

        val saved = repo.save(entity)

        logger.debug("Payment saved successfully with id={}", saved.id)

        return mapper.toResponse(saved)
    }

    @Transactional
    fun create(request: PaymentCreateRequest): ResponsePaymentResponse {
        logger.info(
            "Creating payment: amount={} currency={} status={} creditorId={} debtorId={}",
            request.amount, request.currency, request.status, request.creditorId, request.debtorId
        )

        val entity = mapper.toEntity(request)
        val saved = repo.save(entity)

        logger.debug("Payment saved successfully with id={}", saved.id)

        return mapper.toResponseResponse(saved)
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

    @Transactional
    fun echo(request: PaymentEchoRequest): ResponsePaymentResponse {
        logger.info("Echoing payment (creating duplicate record)")
        val entity = mapper.toEntity(request)
        val saved = repo.save(entity)
        return mapper.toResponseResponse(saved)
    }
}
