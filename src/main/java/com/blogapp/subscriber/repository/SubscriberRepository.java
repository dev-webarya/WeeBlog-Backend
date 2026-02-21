package com.blogapp.subscriber.repository;

import com.blogapp.subscriber.entity.Subscriber;
import com.blogapp.subscriber.enums.SubscriberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberRepository extends MongoRepository<Subscriber, String> {

    Optional<Subscriber> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Subscriber> findByStatus(SubscriberStatus status, Pageable pageable);

    long countByStatus(SubscriberStatus status);

    List<Subscriber> findAllByStatusAndVerified(SubscriberStatus status, boolean verified);
}
