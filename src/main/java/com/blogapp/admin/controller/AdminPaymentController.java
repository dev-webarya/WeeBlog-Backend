package com.blogapp.admin.controller;

import com.blogapp.entitlement.entity.Entitlement;
import com.blogapp.entitlement.repository.EntitlementRepository;
import com.blogapp.payment.entity.Payment;
import com.blogapp.payment.enums.PaymentStatus;
import com.blogapp.payment.repository.PaymentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/finance")
@RequiredArgsConstructor
@Tag(name = "Admin Finance", description = "Full CRUD for admin payment and subscription management")
public class AdminPaymentController {

    private final PaymentRepository paymentRepository;
    private final EntitlementRepository entitlementRepository;

    // ─── PAYMENTS ────────────────────────────────────────────────

    @GetMapping("/payments")
    @Operation(summary = "List payments", description = "Paginated list with optional status filter")
    public ResponseEntity<Page<Payment>> getAllPayments(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (status != null && !status.isBlank()) {
            try {
                PaymentStatus ps = PaymentStatus.valueOf(status.toUpperCase());
                return ResponseEntity.ok(paymentRepository.findByStatus(ps, pageable));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok(paymentRepository.findAll(pageable));
    }

    @GetMapping("/payments/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<Payment> getPaymentById(@PathVariable String id) {
        return paymentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/payments/{id}/status")
    @Operation(summary = "Update payment status", description = "Change status to REFUNDED, FAILED, etc.")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {

        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Payment payment = paymentRepository.findById(id).orElse(null);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            payment.setStatus(PaymentStatus.valueOf(newStatus.toUpperCase()));
            return ResponseEntity.ok(paymentRepository.save(payment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/payments/{id}")
    @Operation(summary = "Delete a payment record")
    public ResponseEntity<Void> deletePayment(@PathVariable String id) {
        if (!paymentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        paymentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ─── ENTITLEMENTS / SUBSCRIPTIONS ────────────────────────────

    @GetMapping("/subscriptions")
    @Operation(summary = "List entitlements", description = "Paginated list with optional active/expired/all filter")
    public ResponseEntity<Page<Entitlement>> getAllSubscriptions(
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return switch (status.toLowerCase()) {
            case "active" -> ResponseEntity.ok(
                    entitlementRepository.findByEndAtIsNullOrEndAtAfter(LocalDateTime.now(), pageable));
            case "expired" -> ResponseEntity.ok(
                    entitlementRepository.findByEndAtBeforeAndEndAtIsNotNull(LocalDateTime.now(), pageable));
            default -> ResponseEntity.ok(entitlementRepository.findAll(pageable));
        };
    }

    @GetMapping("/subscriptions/{id}")
    @Operation(summary = "Get entitlement by ID")
    public ResponseEntity<Entitlement> getSubscriptionById(@PathVariable String id) {
        return entitlementRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/subscriptions/{id}")
    @Operation(summary = "Update entitlement", description = "Extend end date or change scope")
    public ResponseEntity<Entitlement> updateSubscription(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {

        return entitlementRepository.findById(id).map(ent -> {
            if (body.containsKey("endAt")) {
                ent.setEndAt(LocalDateTime.parse(body.get("endAt")));
            }
            if (body.containsKey("scopeId")) {
                ent.setScopeId(body.get("scopeId"));
            }
            return ResponseEntity.ok(entitlementRepository.save(ent));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/subscriptions/{id}")
    @Operation(summary = "Revoke an entitlement")
    public ResponseEntity<Void> deleteSubscription(@PathVariable String id) {
        if (!entitlementRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        entitlementRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
