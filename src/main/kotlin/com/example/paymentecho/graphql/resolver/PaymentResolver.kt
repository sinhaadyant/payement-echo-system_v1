package com.example.paymentecho.graphql.resolver

import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.dto.request.PaymentEchoRequest
import com.example.paymentecho.dto.response.PaymentResponse
import com.example.paymentecho.graphql.model.PaymentPage
import com.example.paymentecho.graphql.model.Payment
import com.example.paymentecho.service.PaymentService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class PaymentResolver(private val paymentService: PaymentService) {

    @QueryMapping
    fun payments(
        @Argument page: Int?,
        @Argument size: Int?,
        @Argument sort: String?
    ): PaymentPage {
        val pageNum = page ?: 0
        val sizeNum = size ?: 20
        val paymentPage = paymentService.findAll(pageNum, sizeNum, sort, null, null, null, null)
        return PaymentPage(
            payments = paymentPage.content.map { it.toGraphQL() },
            total = paymentPage.totalElements.toInt(),
            page = paymentPage.number,
            size = paymentPage.size,
            totalPages = paymentPage.totalPages
        )
    }

    @QueryMapping
    fun payment(@Argument id: String): PaymentResponse {
        return paymentService.findByIdResponse(UUID.fromString(id))
    }

    @QueryMapping
    fun paymentsByStatus(
        @Argument status: String,
        @Argument page: Int?,
        @Argument size: Int?
    ): PaymentPage {
        val pageNum = page ?: 0
        val sizeNum = size ?: 20
        val paymentPage = paymentService.findAll(pageNum, sizeNum, null, status, null, null, null)
        return PaymentPage(
            payments = paymentPage.content.map { it.toGraphQL() },
            total = paymentPage.totalElements.toInt(),
            page = paymentPage.number,
            size = paymentPage.size,
            totalPages = paymentPage.totalPages
        )
    }

    @QueryMapping
    fun paymentsByCurrency(
        @Argument currency: String,
        @Argument page: Int?,
        @Argument size: Int?
    ): PaymentPage {
        val pageNum = page ?: 0
        val sizeNum = size ?: 20
        val paymentPage = paymentService.findAll(pageNum, sizeNum, null, null, currency, null, null)
        return PaymentPage(
            payments = paymentPage.content.map { it.toGraphQL() },
            total = paymentPage.totalElements.toInt(),
            page = paymentPage.number,
            size = paymentPage.size,
            totalPages = paymentPage.totalPages
        )
    }

    @QueryMapping
    fun paymentsByAmountRange(
        @Argument minAmount: Double,
        @Argument maxAmount: Double,
        @Argument page: Int?,
        @Argument size: Int?
    ): PaymentPage {
        val pageNum = page ?: 0
        val sizeNum = size ?: 20
        val paymentPage = paymentService.findAll(pageNum, sizeNum, null, null, null, minAmount, maxAmount)
        return PaymentPage(
            payments = paymentPage.content.map { it.toGraphQL() },
            total = paymentPage.totalElements.toInt(),
            page = paymentPage.number,
            size = paymentPage.size,
            totalPages = paymentPage.totalPages
        )
    }

    @MutationMapping
    fun createPayment(@Argument input: PaymentInput): PaymentResponse {
        val request = PaymentCreateRequest(
            amount = input.amount,
            currency = input.currency,
            status = input.status,
            creditorId = input.creditorId?.let { UUID.fromString(it) },
            debtorId = input.debtorId?.let { UUID.fromString(it) }
        )
        return paymentService.create(request)
    }

    @MutationMapping
    fun echoPayment(@Argument input: PaymentEchoInput): PaymentResponse {
        val request = PaymentEchoRequest(
            amount = input.amount,
            currency = input.currency,
            status = input.status,
            creditorId = input.creditorId?.let { UUID.fromString(it) },
            debtorId = input.debtorId?.let { UUID.fromString(it) }
        )
        return paymentService.echo(request)
    }

    private fun PaymentResponse.toGraphQL(): Payment {
        return Payment(
            id = id.toString(),
            amount = amount,
            currency = currency,
            status = status,
            createdAt = createdAt.toString(),
            creditorId = creditorId?.toString(),
            debtorId = debtorId?.toString()
        )
    }
}

data class PaymentInput(
    val amount: Double,
    val currency: String,
    val status: String,
    val creditorId: String?,
    val debtorId: String?
)

data class PaymentEchoInput(
    val amount: Double,
    val currency: String,
    val status: String,
    val creditorId: String?,
    val debtorId: String?
)
