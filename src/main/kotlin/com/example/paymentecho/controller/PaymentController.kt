package com.example.paymentecho.controller

import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.dto.request.PaymentEchoRequest
import com.example.paymentecho.dto.response.PaymentListResponse
import com.example.paymentecho.dto.response.PaymentResponse
import com.example.paymentecho.service.PaymentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * REST controller for payment management operations.
 * 
 * Supports internationalization (i18n) via Accept-Language header.
 * Default language: Hindi (hi)
 * 
 * All endpoints support pagination, filtering, and sorting.
 * 
 * @param service The payment service for business logic
 */
@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment management API with i18n support")
class PaymentController(private val service: PaymentService) {

    @GetMapping
    @Operation(
        summary = "Get all payments",
        description = "Retrieve a paginated list of all payments. Supports Accept-Language header for i18n (default: hi - Hindi)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved payments")
        ]
    )
    fun getAll(
        @Parameter(description = "Page number (0-indexed)", example = "0")
        @RequestParam(defaultValue = "0") page: Int,
        
        @Parameter(description = "Page size", example = "20")
        @RequestParam(defaultValue = "20") size: Int,
        
        @Parameter(description = "Sort field and direction", example = "createdAt,desc")
        @RequestParam(required = false) sort: String?,
        
        @Parameter(description = "Filter by payment status", example = "RECEIVED")
        @RequestParam(required = false) status: String?,
        
        @Parameter(description = "Filter by currency", example = "USD")
        @RequestParam(required = false) currency: String?,
        
        @Parameter(description = "Minimum amount filter", example = "100.0")
        @RequestParam(required = false) minAmount: Double?,
        
        @Parameter(description = "Maximum amount filter", example = "1000.0")
        @RequestParam(required = false) maxAmount: Double?,
        
        @Parameter(description = "Start date filter (ISO format)", example = "2025-01-01T00:00:00Z")
        @RequestParam(required = false) startDate: java.time.Instant?,
        
        @Parameter(description = "End date filter (ISO format)", example = "2025-12-31T23:59:59Z")
        @RequestParam(required = false) endDate: java.time.Instant?,
        
        @Parameter(description = "Language preference (hi, en, es, fr, de, bn, ta, te, kn, ru, zh). Default: hi (Hindi)", example = "hi")
        @RequestHeader(name = "Accept-Language", required = false, defaultValue = "hi") acceptLanguage: String
    ): ResponseEntity<PaymentListResponse> {
        val payments = service.findAll(page, size, sort, status, currency, minAmount, maxAmount, startDate, endDate)
        return ResponseEntity.ok(PaymentListResponse(payments = payments.content, total = payments.totalElements.toInt()))
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get payment by ID",
        description = "Retrieve a specific payment by its UUID. Supports Accept-Language header for i18n (default: en)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Payment found"),
            ApiResponse(responseCode = "404", description = "Payment not found", content = [Content()])
        ]
    )
    fun getById(
        @Parameter(description = "Payment UUID", required = true)
        @PathVariable id: UUID,
        
        @Parameter(description = "Language preference (hi, en, es, fr, de, bn, ta, te, kn, ru, zh). Default: hi (Hindi)", example = "hi")
        @RequestHeader(name = "Accept-Language", required = false, defaultValue = "hi") acceptLanguage: String
    ): ResponseEntity<PaymentResponse> {
        return ResponseEntity.ok(service.findById(id))
    }

    @PostMapping
    @Operation(
        summary = "Create a new payment",
        description = "Create a new payment with the provided details.\n\n" +
                "⚠️ IMPORTANT: If using creditorId or debtorId, you must first:\n" +
                "1. Call GET /api/v1/creditors to get a valid creditor ID\n" +
                "2. Call GET /api/v1/debtors to get a valid debtor ID\n" +
                "3. Use those IDs in the request\n\n" +
                "Sample data examples:\n" +
                "- Payment with creditor/debtor: amount=1500.00, currency=USD, status=RECEIVED\n" +
                "- Standalone payment: amount=1000.00, currency=USD, status=RECEIVED (no creditorId/debtorId)\n\n" +
                "Supports Accept-Language header for i18n (default: hi - Hindi)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Payment created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Creditor or Debtor not found if invalid IDs provided", content = [Content()])
        ]
    )
    fun create(
        @Valid
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Payment creation request.\n\n" +
                    "⚠️ NOTE: Replace placeholder UUIDs with actual IDs from GET /api/v1/creditors and GET /api/v1/debtors endpoints.\n\n" +
                    "Example with sample data:\n" +
                    "{\n" +
                    "  \"amount\": 1500.00,\n" +
                    "  \"currency\": \"USD\",\n" +
                    "  \"status\": \"RECEIVED\",\n" +
                    "  \"creditorId\": \"<REPLACE-WITH-ACTUAL-CREDITOR-ID>\",\n" +
                    "  \"debtorId\": \"<REPLACE-WITH-ACTUAL-DEBTOR-ID>\"\n" +
                    "}",
            required = true,
            content = [Content(
                mediaType = "application/json",
                examples = [
                    io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Payment with Creditor/Debtor",
                        value = """{"amount": 1500.00, "currency": "USD", "status": "RECEIVED", "creditorId": "REPLACE-WITH-ACTUAL-CREDITOR-ID", "debtorId": "REPLACE-WITH-ACTUAL-DEBTOR-ID"}""",
                        summary = "⚠️ Replace IDs with actual values from GET endpoints"
                    ),
                    io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Standalone Payment (Recommended for Testing)",
                        value = """{"amount": 1000.00, "currency": "USD", "status": "RECEIVED"}""",
                        summary = "Payment without creditor/debtor - works immediately"
                    )
                ]
            )]
        )
        @RequestBody request: PaymentCreateRequest,
        
        @Parameter(description = "Language preference (hi, en, es, fr, de, bn, ta, te, kn, ru, zh). Default: hi (Hindi)", example = "hi")
        @RequestHeader(name = "Accept-Language", required = false, defaultValue = "hi") acceptLanguage: String
    ): ResponseEntity<PaymentResponse> {
        val created = service.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PostMapping("/echo")
    @Operation(
        summary = "Echo a payment",
        description = "Echo a payment (returns the same payment data and saves it). Sample example: amount=2000.00, currency=EUR, status=PROCESSING. Supports Accept-Language header for i18n (default: en)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Payment echoed successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()])
        ]
    )
    fun echo(
        @Valid
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Payment echo request. Example: {\"amount\": 2000.00, \"currency\": \"EUR\", \"status\": \"PROCESSING\"}",
            required = true,
            content = [Content(
                mediaType = "application/json",
                examples = [
                    io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Echo Payment Example",
                        value = """{"amount": 2000.00, "currency": "EUR", "status": "PROCESSING"}""",
                        summary = "Echo payment sample"
                    )
                ]
            )]
        )
        @RequestBody request: PaymentEchoRequest,
        
        @Parameter(description = "Language preference (hi, en, es, fr, de, bn, ta, te, kn, ru, zh). Default: hi (Hindi)", example = "hi")
        @RequestHeader(name = "Accept-Language", required = false, defaultValue = "hi") acceptLanguage: String
    ): ResponseEntity<PaymentResponse> {
        return ResponseEntity.ok(service.echo(request))
    }
}

