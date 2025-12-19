package com.example.paymentecho.repository

import com.example.paymentecho.entity.Debtor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DebtorRepository : JpaRepository<Debtor, UUID>
