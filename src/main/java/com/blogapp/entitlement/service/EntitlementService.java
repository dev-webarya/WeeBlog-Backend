package com.blogapp.entitlement.service;

import com.blogapp.entitlement.entity.Entitlement;
import com.blogapp.entitlement.enums.EntitlementType;

import java.time.LocalDateTime;
import java.util.List;

public interface EntitlementService {

    /**
     * Check whether the given user can access part-2 of a premium blog.
     * Checks: PER_BLOG → SUBSCRIPTION_SUBSECTION → SUBSCRIPTION_SECTION →
     * SUBSCRIPTION_ALL
     */
    boolean hasAccess(String userId, String blogId, String sectionId, String subsectionId);

    /**
     * Get all active entitlements for a user.
     */
    List<Entitlement> getActiveEntitlements(String userId);

    /**
     * Grant an entitlement (called after payment success).
     */
    Entitlement grant(String userId, EntitlementType type, String scopeId,
            String blogId, String paymentId,
            LocalDateTime startAt, LocalDateTime endAt);
}
