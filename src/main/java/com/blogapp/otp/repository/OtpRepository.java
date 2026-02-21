package com.blogapp.otp.repository;

import com.blogapp.otp.entity.OtpVerification;
import com.blogapp.otp.enums.OtpPurpose;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends MongoRepository<OtpVerification, String> {

    Optional<OtpVerification> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, OtpPurpose purpose);

    void deleteByEmailAndPurpose(String email, OtpPurpose purpose);
}
