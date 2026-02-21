package com.blogapp.entitlement.service.impl;

import com.blogapp.entitlement.entity.Entitlement;
import com.blogapp.entitlement.enums.EntitlementType;
import com.blogapp.entitlement.repository.EntitlementRepository;
import com.blogapp.entitlement.service.EntitlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntitlementServiceImpl implements EntitlementService {

    private final EntitlementRepository entitlementRepository;

    @Override
    public boolean hasAccess(String userId, String blogId, String sectionId, String subsectionId) {
        if (userId == null)
            return false;

        List<Entitlement> entitlements = getActiveEntitlements(userId);

        for (Entitlement e : entitlements) {
            switch (e.getType()) {
                case SUBSCRIPTION_ALL:
                    return true;

                case SUBSCRIPTION_SECTION:
                    if (sectionId != null && sectionId.equals(e.getScopeId()))
                        return true;
                    break;

                case SUBSCRIPTION_SUBSECTION:
                    if (subsectionId != null && subsectionId.equals(e.getScopeId()))
                        return true;
                    break;

                case PER_BLOG:
                    if (blogId != null && blogId.equals(e.getBlogId()))
                        return true;
                    break;
            }
        }
        return false;
    }

    @Override
    public List<Entitlement> getActiveEntitlements(String userId) {
        LocalDateTime now = LocalDateTime.now();
        return entitlementRepository.findByUserId(userId).stream()
                .filter(e -> e.getEndAt() == null || e.getEndAt().isAfter(now))
                .collect(Collectors.toList());
    }

    @Override
    public Entitlement grant(String userId, EntitlementType type, String scopeId,
            String blogId, String paymentId,
            LocalDateTime startAt, LocalDateTime endAt) {
        Entitlement entitlement = Entitlement.builder()
                .userId(userId)
                .type(type)
                .scopeId(scopeId)
                .blogId(blogId)
                .paymentId(paymentId)
                .startAt(startAt != null ? startAt : LocalDateTime.now())
                .endAt(endAt)
                .build();

        log.info("Granting entitlement: type={}, userId={}, scopeId={}, blogId={}",
                type, userId, scopeId, blogId);
        return entitlementRepository.save(entitlement);
    }
}
