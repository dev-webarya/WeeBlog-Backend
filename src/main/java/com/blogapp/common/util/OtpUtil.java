package com.blogapp.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class OtpUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private OtpUtil() {
        // Utility class — prevent instantiation
    }

    /**
     * Generate a numeric OTP of the specified length (e.g., 6 digits → "149523").
     */
    public static String generateOtp(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(RANDOM.nextInt(10));
        }
        return otp.toString();
    }

    /**
     * Hash an OTP using SHA-256 for secure storage.
     */
    public static String hashOtp(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verify a raw OTP against its stored hash.
     */
    public static boolean verifyOtp(String rawOtp, String storedHash) {
        return hashOtp(rawOtp).equals(storedHash);
    }
}
