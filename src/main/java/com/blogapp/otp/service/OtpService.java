package com.blogapp.otp.service;

import com.blogapp.otp.enums.OtpPurpose;

public interface OtpService {

    void sendOtp(String email, OtpPurpose purpose);

    boolean verifyOtp(String email, String otp, OtpPurpose purpose);

    boolean isEmailVerified(String email, OtpPurpose purpose);
}
