package com.blogapp.entitlement.entity;

import com.blogapp.entitlement.enums.EntitlementType;
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
@Document(collection = "entitlements")
public class Entitlement {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private EntitlementType type;

    // For PER_BLOG: blogId; for SUBSCRIPTION_SUBSECTION: subsectionId;
    // for SUBSCRIPTION_SECTION: sectionId; for SUBSCRIPTION_ALL: null
    private String scopeId;

    // Only for PER_BLOG purchases
    private String blogId;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    // Reference to the Payment that granted this entitlement
    private String paymentId;

    @CreatedDate
    private LocalDateTime createdAt;
}
