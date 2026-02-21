package com.blogapp.payment.controller;

import com.blogapp.payment.dto.request.CheckoutRequest;
import com.blogapp.payment.dto.request.VerifyPaymentRequest;
import com.blogapp.payment.entity.Payment;
import com.blogapp.payment.service.PaymentService;
import com.blogapp.user.entity.User;
import com.razorpay.RazorpayException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@Tag(name = "Checkout", description = "Payment and checkout flow for premium content")
public class CheckoutController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    @Operation(summary = "Create a Razorpay order", description = "Initiates a Razorpay order for the selected plan. Returns order details for the frontend payment dialog.")
    public ResponseEntity<Payment> createOrder(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CheckoutRequest request) throws RazorpayException {

        Payment payment = paymentService.createOrder(
                user.getId(),
                request.getPlanType(),
                request.getDuration(),
                request.getScopeId(),
                request.getBlogId());

        return ResponseEntity.ok(payment);
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify payment", description = "Verify Razorpay payment and grant entitlement to the user")
    public ResponseEntity<Payment> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {

        Payment payment = paymentService.verifyAndGrant(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature());

        return ResponseEntity.ok(payment);
    }
}
