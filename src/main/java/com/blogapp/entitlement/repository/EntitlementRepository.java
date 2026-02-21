package com.blogapp.entitlement.repository;

import com.blogapp.entitlement.entity.Entitlement;
import com.blogapp.entitlement.enums.EntitlementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EntitlementRepository extends MongoRepository<Entitlement, String> {

    List<Entitlement> findByUserId(String userId);

    List<Entitlement> findByUserIdAndType(String userId, EntitlementType type);

    // Active entitlements: endAt is null (lifetime) or endAt > now
    List<Entitlement> findByUserIdAndEndAtIsNullOrUserIdAndEndAtAfter(
            String userId1, String userId2, LocalDateTime now);

    boolean existsByUserIdAndBlogIdAndType(String userId, String blogId, EntitlementType type);

    boolean existsByUserIdAndScopeIdAndType(String userId, String scopeId, EntitlementType type);

    // Admin filters
    Page<Entitlement> findByEndAtIsNullOrEndAtAfter(LocalDateTime now, Pageable pageable);

    Page<Entitlement> findByEndAtBeforeAndEndAtIsNotNull(LocalDateTime now, Pageable pageable);
}
