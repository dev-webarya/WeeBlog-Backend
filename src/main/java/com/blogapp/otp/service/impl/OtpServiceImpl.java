package com.blogapp.otp.service.impl;

import com.blogapp.common.exception.OtpVerificationException;
import com.blogapp.common.util.OtpUtil;
import com.blogapp.otp.entity.OtpVerification;
import com.blogapp.otp.enums.OtpPurpose;
import com.blogapp.otp.repository.OtpRepository;
import com.blogapp.otp.service.EmailService;
import com.blogapp.otp.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    @Value("${blog.otp.length:6}")
    private int otpLength;

    @Value("${blog.otp.expiry-minutes:10}")
    private int expiryMinutes;

    @Value("${blog.otp.max-attempts:5}")
    private int maxAttempts;

    @Value("${blog.otp.resend-cooldown-seconds:60}")
    private int resendCooldownSeconds;

    @Override
    public void sendOtp(String email, OtpPurpose purpose) {
        // Check resend cooldown
        Optional<OtpVerification> existing = otpRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose);

        if (existing.isPresent()) {
            OtpVerification prev = existing.get();
            LocalDateTime cooldownEnd = prev.getCreatedAt().plusSeconds(resendCooldownSeconds);
            if (LocalDateTime.now().isBefore(cooldownEnd)) {
                throw new OtpVerificationException(
                        "Please wait " + resendCooldownSeconds + " seconds before requesting a new OTP.");
            }
        }

        // Generate OTP
        String rawOtp = OtpUtil.generateOtp(otpLength);
        String hashedOtp = OtpUtil.hashOtp(rawOtp);

        // Save OTP record
        OtpVerification otpVerification = OtpVerification.builder()
                .email(email)
                .otpHash(hashedOtp)
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(expiryMinutes))
                .attemptsCount(0)
                .build();

        otpRepository.save(otpVerification);

        // Send email
        String subject = purpose == OtpPurpose.BLOG_SUBMISSION
                ? "Your OTP for Blog Submission"
                : "Your OTP for Blog Subscription";

        String body = buildOtpEmailBody(rawOtp, purpose);
        emailService.sendEmail(email, subject, body);

        log.info("OTP sent to {} for purpose {}", email, purpose);
    }

    @Override
    public boolean verifyOtp(String email, String otp, OtpPurpose purpose) {
        OtpVerification otpRecord = otpRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .orElseThrow(
                        () -> new OtpVerificationException("No OTP found for this email. Please request a new one."));

        // Check if already verified
        if (otpRecord.getVerifiedAt() != null) {
            throw new OtpVerificationException("OTP already verified.");
        }

        // Check expiry
        if (LocalDateTime.now().isAfter(otpRecord.getExpiresAt())) {
            throw new OtpVerificationException("OTP has expired. Please request a new one.");
        }

        // Check max attempts
        if (otpRecord.getAttemptsCount() >= maxAttempts) {
            throw new OtpVerificationException("Maximum OTP attempts exceeded. Please request a new OTP.");
        }

        // Increment attempt count
        otpRecord.setAttemptsCount(otpRecord.getAttemptsCount() + 1);

        // Verify OTP
        if (!OtpUtil.verifyOtp(otp, otpRecord.getOtpHash())) {
            otpRepository.save(otpRecord);
            int remaining = maxAttempts - otpRecord.getAttemptsCount();
            throw new OtpVerificationException("Invalid OTP. " + remaining + " attempts remaining.");
        }

        // Mark as verified
        otpRecord.setVerifiedAt(LocalDateTime.now());
        otpRepository.save(otpRecord);

        log.info("OTP verified for {} purpose {}", email, purpose);
        return true;
    }

    @Override
    public boolean isEmailVerified(String email, OtpPurpose purpose) {
        Optional<OtpVerification> otpRecord = otpRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose);
        return otpRecord.isPresent() && otpRecord.get().getVerifiedAt() != null;
    }

    private String buildOtpEmailBody(String otp, OtpPurpose purpose) {
        String purposeText = purpose == OtpPurpose.BLOG_SUBMISSION
                ? "blog submission"
                : "blog subscription";

        return """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2>OTP Verification</h2>
                    <p>Your OTP for %s is:</p>
                    <h1 style="color: #4A90D9; letter-spacing: 8px; font-size: 36px;">%s</h1>
                    <p>This OTP is valid for <strong>%d minutes</strong>.</p>
                    <p style="color: #888; font-size: 12px;">
                        If you did not request this, please ignore this email.
                    </p>
                </body>
                </html>
                """.formatted(purposeText, otp, expiryMinutes);
    }
}
