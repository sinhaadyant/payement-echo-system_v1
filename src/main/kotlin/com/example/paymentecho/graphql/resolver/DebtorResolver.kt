package com.example.paymentecho.graphql.resolver

import com.example.paymentecho.dto.request.DebtorCreateRequest
import com.example.paymentecho.dto.response.DebtorResponse
import com.example.paymentecho.graphql.model.DebtorPage
import com.example.paymentecho.graphql.model.Debtor
import com.example.paymentecho.service.DebtorService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class DebtorResolver(private val debtorService: DebtorService) {

    @QueryMapping
    fun debtors(
        @Argument page: Int?,
        @Argument size: Int?,
        @Argument sort: String?
    ): DebtorPage {
        val pageNum = page ?: 0
        val sizeNum = size ?: 20
        val debtorPage = debtorService.findAll(pageNum, sizeNum, sort, null, null)
        return DebtorPage(
            debtors = debtorPage.content.map { it.toGraphQL() },
            total = debtorPage.totalElements.toInt(),
            page = debtorPage.number,
            size = debtorPage.size,
            totalPages = debtorPage.totalPages
        )
    }

    @QueryMapping
    fun debtor(@Argument id: String): DebtorResponse {
        return debtorService.findById(UUID.fromString(id))
    }

    @QueryMapping
    fun debtorsByName(
        @Argument name: String,
        @Argument page: Int?,
        @Argument size: Int?
    ): DebtorPage {
        val pageNum = page ?: 0
        val sizeNum = size ?: 20
        val debtorPage = debtorService.findAll(pageNum, sizeNum, null, name, null)
        return DebtorPage(
            debtors = debtorPage.content.map { it.toGraphQL() },
            total = debtorPage.totalElements.toInt(),
            page = debtorPage.number,
            size = debtorPage.size,
            totalPages = debtorPage.totalPages
        )
    }

    @QueryMapping
    fun debtorsByBankCode(
        @Argument bankCode: String,
        @Argument page: Int?,
        @Argument size: Int?
    ): DebtorPage {
        val pageNum = page ?: 0
        val sizeNum = size ?: 20
        val debtorPage = debtorService.findAll(pageNum, sizeNum, null, null, bankCode)
        return DebtorPage(
            debtors = debtorPage.content.map { it.toGraphQL() },
            total = debtorPage.totalElements.toInt(),
            page = debtorPage.number,
            size = debtorPage.size,
            totalPages = debtorPage.totalPages
        )
    }

    @MutationMapping
    fun createDebtor(@Argument input: DebtorInput): DebtorResponse {
        val request = DebtorCreateRequest(
            name = input.name,
            accountNumber = input.accountNumber,
            bankCode = input.bankCode,
            address = input.address,
            email = input.email
        )
        return debtorService.create(request)
    }

    @MutationMapping
    fun deleteDebtor(@Argument id: String): Boolean {
        debtorService.delete(UUID.fromString(id))
        return true
    }

    private fun DebtorResponse.toGraphQL(): Debtor {
        return Debtor(
            id = id.toString(),
            name = name,
            accountNumber = accountNumber,
            bankCode = bankCode,
            address = address,
            email = email,
            createdAt = createdAt.toString(),
            updatedAt = updatedAt.toString()
        )
    }
}

data class DebtorInput(
    val name: String,
    val accountNumber: String,
    val bankCode: String,
    val address: String?,
    val email: String?
)
