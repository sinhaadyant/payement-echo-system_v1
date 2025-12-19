package com.example.paymentecho.controller

import com.example.paymentecho.dto.PaymentRequest
import com.example.paymentecho.dto.PaymentResponse
import com.example.paymentecho.service.PaymentService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

/**
 * Payment REST controller.
 * - Controllers should be small: validate request, call service, map/return response.
 * - Keep controller free of business logic.
 */
@RestController
@RequestMapping("/api/payments")
@Validated
class PaymentController(private val service: PaymentService) {

    @GetMapping
    fun all(): List<PaymentResponse> = service.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<PaymentResponse> =
        ResponseEntity.ok(service.findById(id))

    @PostMapping
    fun create(@Valid @RequestBody req: PaymentRequest): ResponseEntity<PaymentResponse> {
        val created = service.create(req)
        return ResponseEntity.created(URI.create("/api/payments/${created.id}")).body(created)
    }

    @PostMapping("/echo")
    fun echo(@Valid @RequestBody req: PaymentRequest): ResponseEntity<PaymentResponse> =
        ResponseEntity.ok(service.echo(req))
}

