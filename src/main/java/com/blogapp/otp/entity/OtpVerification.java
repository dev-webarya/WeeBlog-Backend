package com.blogapp.otp.entity;

import com.blogapp.otp.enums.OtpPurpose;
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
@Document(collection = "otp_verifications")
public class OtpVerification {

    @Id
    private String id;

    @Indexed
    private String email;

    private String otpHash;

    private OtpPurpose purpose;

    private LocalDateTime expiresAt;

    @Builder.Default
    private int attemptsCount = 0;

    private LocalDateTime verifiedAt;

    @CreatedDate
    private LocalDateTime createdAt;
}
