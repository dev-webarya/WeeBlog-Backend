package com.blogapp.auth.controller;

import com.blogapp.auth.dto.response.AuthResponse;
import com.blogapp.entitlement.entity.Entitlement;
import com.blogapp.entitlement.service.EntitlementService;
import com.blogapp.payment.entity.Payment;
import com.blogapp.payment.repository.PaymentRepository;
import com.blogapp.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Tag(name = "Account", description = "Logged-in user endpoints")
public class AccountController {

    private final EntitlementService entitlementService;
    private final PaymentRepository paymentRepository;

    @GetMapping("/me")
    @Operation(summary = "Get my profile", description = "Returns the authenticated user's profile info")
    public ResponseEntity<AuthResponse.UserInfo> getMyProfile(@AuthenticationPrincipal User user) {
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .emailVerified(user.getEmailVerifiedAt() != null)
                .build();

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/entitlements")
    @Operation(summary = "Get my entitlements", description = "Returns the authenticated user's active content entitlements")
    public ResponseEntity<List<Entitlement>> getMyEntitlements(@AuthenticationPrincipal User user) {
        List<Entitlement> entitlements = entitlementService.getActiveEntitlements(user.getId());
        return ResponseEntity.ok(entitlements);
    }

    @GetMapping("/payments")
    @Operation(summary = "Get my billing history", description = "Returns the authenticated user's past payments")
    public ResponseEntity<Page<Payment>> getMyPayments(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Payment> payments = paymentRepository.findByUserId(
                user.getId(),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(payments);
    }
}
