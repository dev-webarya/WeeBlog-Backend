package com.blogapp.payment.entity;

import com.blogapp.entitlement.enums.EntitlementType;
import com.blogapp.payment.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String provider; // e.g., "razorpay"

    @Indexed
    private String providerOrderId; // Razorpay order_id
    private String providerPaymentId; // Razorpay payment_id

    private long amountPaise;
    private String currency; // "INR"
    private long taxPaise;

    @Indexed
    @Builder.Default
    private PaymentStatus status = PaymentStatus.CREATED;

    // What are they buying?
    private EntitlementType planType; // PER_BLOG, SUBSCRIPTION_SUBSECTION, etc.
    private String planDuration; // null for PER_BLOG, "1M", "3M", "6M", "12M" for subscriptions
    private String scopeId; // sectionId / subsectionId
    private String blogId; // for PER_BLOG only

    @CreatedDate
    private LocalDateTime createdAt;
}
