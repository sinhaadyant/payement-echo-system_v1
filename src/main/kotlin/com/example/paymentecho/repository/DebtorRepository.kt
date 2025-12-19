package com.example.paymentecho.repository

import com.example.paymentecho.entity.Debtor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DebtorRepository : JpaRepository<Debtor, UUID> {
    
    @Query("SELECT d FROM Debtor d WHERE (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND (:bankCode IS NULL OR d.bankCode = :bankCode)")
    fun findAllFiltered(@Param("name") name: String?, @Param("bankCode") bankCode: String?, pageable: Pageable): Page<Debtor>
}
