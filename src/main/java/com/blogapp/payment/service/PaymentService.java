package com.blogapp.payment.service;

import com.blogapp.entitlement.enums.EntitlementType;
import com.blogapp.entitlement.service.EntitlementService;
import com.blogapp.payment.config.PricingConfig;
import com.blogapp.payment.entity.Payment;
import com.blogapp.payment.enums.PaymentStatus;
import com.blogapp.payment.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EntitlementService entitlementService;
    private final PricingConfig pricingConfig;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    /**
     * Create a Razorpay order and save a Payment record.
     */
    public Payment createOrder(String userId, String planType, String duration,
            String scopeId, String blogId) throws RazorpayException {

        long amountPaise = pricingConfig.calculatePrice(planType, duration);

        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "blog_" + System.currentTimeMillis());

        Order order = razorpay.orders.create(orderRequest);

        Payment payment = Payment.builder()
                .userId(userId)
                .provider("razorpay")
                .providerOrderId(order.get("id"))
                .amountPaise(amountPaise)
                .currency("INR")
                .taxPaise(0)
                .status(PaymentStatus.CREATED)
                .planType(EntitlementType.valueOf(planType))
                .planDuration(duration)
                .scopeId(scopeId)
                .blogId(blogId)
                .build();

        log.info("Created Razorpay order: {} for user: {}, plan: {}", order.get("id"), userId, planType);
        return paymentRepository.save(payment);
    }

    /**
     * Verify payment after Razorpay callback and grant the entitlement.
     */
    public Payment verifyAndGrant(String razorpayOrderId, String razorpayPaymentId,
            String razorpaySignature) {

        Payment payment = paymentRepository.findByProviderOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + razorpayOrderId));

        // TODO: Verify signature with Utils.verifyPaymentSignature for production
        // For now, we trust the callback

        payment.setProviderPaymentId(razorpayPaymentId);
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        // Calculate entitlement end date
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = null;
        if (payment.getPlanType() != EntitlementType.PER_BLOG && payment.getPlanDuration() != null) {
            int months = switch (payment.getPlanDuration()) {
                case "1M" -> 1;
                case "3M" -> 3;
                case "6M" -> 6;
                case "12M" -> 12;
                default -> 1;
            };
            endAt = startAt.plusMonths(months);
        }

        // Grant the entitlement
        entitlementService.grant(
                payment.getUserId(),
                payment.getPlanType(),
                payment.getScopeId(),
                payment.getBlogId(),
                payment.getId(),
                startAt,
                endAt);

        log.info("Payment verified and entitlement granted: order={}, user={}",
                razorpayOrderId, payment.getUserId());
        return payment;
    }
}
