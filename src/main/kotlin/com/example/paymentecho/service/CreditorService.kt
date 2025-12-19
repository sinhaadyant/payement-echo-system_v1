package com.example.paymentecho.service

import com.example.paymentecho.dto.request.CreditorCreateRequest
import com.example.paymentecho.dto.response.CreditorResponse
import com.example.paymentecho.exception.CreditorNotFoundException
import com.example.paymentecho.mapper.CreditorMapper
import com.example.paymentecho.repository.CreditorRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Creditor service.
 *
 * TODO:
 *  - Add caching (Caffeine/Redis) for creditor lookups
 *  - Add KYC/verification calls
 *  - Add transactional boundaries and isolation levels if updates are required
 */
@Service
class CreditorService(
    private val repo: CreditorRepository,
    private val mapper: CreditorMapper
) {

    fun findAll(page: Int, size: Int, sort: String?, name: String?, bankCode: String?): Page<CreditorResponse> {
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

    fun findById(id: UUID): CreditorResponse {
        return repo.findById(id)
            .map { mapper.toResponse(it) }
            .orElseThrow { CreditorNotFoundException(id) }
    }

    @Transactional
    fun create(request: CreditorCreateRequest): CreditorResponse {
        val entity = mapper.toEntity(request)
        val saved = repo.save(entity)
        return mapper.toResponse(saved)
    }

    @Transactional
    fun delete(id: UUID) {
        if (!repo.existsById(id)) {
            throw CreditorNotFoundException(id)
        }
        repo.deleteById(id)
    }
}
