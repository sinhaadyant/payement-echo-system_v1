package com.example.paymentecho.repository

import com.example.paymentecho.entity.Creditor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CreditorRepository : JpaRepository<Creditor, UUID> {
    
    @Query("SELECT c FROM Creditor c WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND (:bankCode IS NULL OR c.bankCode = :bankCode)")
    fun findAllFiltered(@Param("name") name: String?, @Param("bankCode") bankCode: String?, pageable: Pageable): Page<Creditor>
}
