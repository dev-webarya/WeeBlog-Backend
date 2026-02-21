package com.blogapp.payment.repository;

import com.blogapp.payment.entity.Payment;
import com.blogapp.payment.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findByUserId(String userId);

    Page<Payment> findByUserId(String userId, Pageable pageable);

    List<Payment> findByUserIdAndStatus(String userId, PaymentStatus status);

    Optional<Payment> findByProviderOrderId(String providerOrderId);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
}
