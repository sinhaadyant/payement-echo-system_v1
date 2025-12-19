package com.example.paymentecho.graphql.resolver

import com.example.paymentecho.dto.request.CreditorCreateRequest
import com.example.paymentecho.dto.response.CreditorResponse
import com.example.paymentecho.graphql.model.CreditorPage
import com.example.paymentecho.graphql.model.Creditor
import com.example.paymentecho.service.CreditorService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class CreditorResolver(private val creditorService: CreditorService) {

    @QueryMapping
    fun creditors(
        @Argument page: Int?,
        @Argument size: Int?,
        @Argument sort: String?
    ): CreditorPage {
        val pageNum = page ?: 0
        val sizeNum = size ?: 20
        val creditorPage = creditorService.findAll(pageNum, sizeNum, sort, null, null)
        return CreditorPage(
            creditors = creditorPage.content.map { it.toGraphQL() },
            total = creditorPage.totalElements.toInt(),
            page = creditorPage.number,
            size = creditorPage.size,
            totalPages = creditorPage.totalPages
        )
    }

    @QueryMapping
    fun creditor(@Argument id: String): CreditorResponse {
        return creditorService.findById(UUID.fromString(id))
    }

    @QueryMapping
    fun creditorsByName(
        @Argument name: String,
        @Argument page: Int?,
        @Argument size: Int?
    ): CreditorPage {
        val pageNum = page ?: 0
        val sizeNum = size ?: 20
        val creditorPage = creditorService.findAll(pageNum, sizeNum, null, name, null)
        return CreditorPage(
            creditors = creditorPage.content.map { it.toGraphQL() },
            total = creditorPage.totalElements.toInt(),
            page = creditorPage.number,
            size = creditorPage.size,
            totalPages = creditorPage.totalPages
        )
    }

    @QueryMapping
    fun creditorsByBankCode(
        @Argument bankCode: String,
        @Argument page: Int?,
        @Argument size: Int?
    ): CreditorPage {
        val pageNum = page ?: 0
        val sizeNum = size ?: 20
        val creditorPage = creditorService.findAll(pageNum, sizeNum, null, null, bankCode)
        return CreditorPage(
            creditors = creditorPage.content.map { it.toGraphQL() },
            total = creditorPage.totalElements.toInt(),
            page = creditorPage.number,
            size = creditorPage.size,
            totalPages = creditorPage.totalPages
        )
    }

    @MutationMapping
    fun createCreditor(@Argument input: CreditorInput): CreditorResponse {
        val request = CreditorCreateRequest(
            name = input.name,
            accountNumber = input.accountNumber,
            bankCode = input.bankCode,
            address = input.address,
            email = input.email
        )
        return creditorService.create(request)
    }

    @MutationMapping
    fun deleteCreditor(@Argument id: String): Boolean {
        creditorService.delete(UUID.fromString(id))
        return true
    }

    private fun CreditorResponse.toGraphQL(): Creditor {
        return Creditor(
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

data class CreditorInput(
    val name: String,
    val accountNumber: String,
    val bankCode: String,
    val address: String?,
    val email: String?
)
