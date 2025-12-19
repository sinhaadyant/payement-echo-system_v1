package com.example.paymentecho.service

import com.example.paymentecho.dto.request.DebtorCreateRequest
import com.example.paymentecho.dto.response.DebtorResponse
import com.example.paymentecho.exception.DebtorNotFoundException
import com.example.paymentecho.mapper.DebtorMapper
import com.example.paymentecho.repository.DebtorRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Debtor service.
 * Mirror the responsibilities of CreditorService — separated for SRP and future divergence.
 */
@Service
class DebtorService(
    private val repo: DebtorRepository,
    private val mapper: DebtorMapper
) {

    fun findAll(page: Int, size: Int, sort: String?, name: String?, bankCode: String?): Page<DebtorResponse> {
        val sortObj = if (sort != null) {
            val parts = sort.split(",")
            if (parts.size == 2) {
                Sort.by(if (parts[1].lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC, parts[0])
            } else {
                Sort.by(Sort.Direction.ASC, "name")
            }
        } else {
            Sort.by(Sort.Direction.ASC, "name")
        }
        val pageable: Pageable = PageRequest.of(page, size, sortObj)
        val entityPage = repo.findAllFiltered(name, bankCode, pageable)
        return entityPage.map { mapper.toResponse(it) }
    }

    fun findById(id: UUID): DebtorResponse {
        return repo.findById(id)
            .map { mapper.toResponse(it) }
            .orElseThrow { DebtorNotFoundException(id) }
    }

    @Transactional
    fun create(request: DebtorCreateRequest): DebtorResponse {
        val entity = mapper.toEntity(request)
        val saved = repo.save(entity)
        return mapper.toResponse(saved)
    }

    @Transactional
    fun delete(id: UUID) {
        if (!repo.existsById(id)) {
            throw DebtorNotFoundException(id)
        }
        repo.deleteById(id)
    }
}
