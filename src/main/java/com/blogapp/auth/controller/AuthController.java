package com.blogapp.auth.controller;

import com.blogapp.auth.dto.request.AuthStartRequest;
import com.blogapp.auth.dto.request.AuthVerifyRequest;
import com.blogapp.auth.dto.response.AuthResponse;
import com.blogapp.config.JwtTokenProvider;
import com.blogapp.otp.enums.OtpPurpose;
import com.blogapp.otp.service.OtpService;
import com.blogapp.user.entity.User;
import com.blogapp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User login via email OTP")
public class AuthController {

    private final OtpService otpService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/start")
    @Operation(summary = "Start login — sends OTP to the given email")
    public ResponseEntity<Map<String, String>> startLogin(
            @Valid @RequestBody AuthStartRequest request) {

        // Ensure user record exists (idempotent)
        userService.findOrCreateByEmail(request.getEmail());

        // Send OTP
        otpService.sendOtp(request.getEmail(), OtpPurpose.USER_LOGIN);

        return ResponseEntity.ok(Map.of(
                "message", "OTP sent to " + request.getEmail()));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify OTP — returns JWT token and user info")
    public ResponseEntity<AuthResponse> verifyOtp(
            @Valid @RequestBody AuthVerifyRequest request) {

        boolean valid = otpService.verifyOtp(
                request.getEmail(), request.getOtp(), OtpPurpose.USER_LOGIN);

        if (!valid) {
            return ResponseEntity.badRequest().build();
        }

        // Mark email as verified and update profile if provided
        User user = userService.findOrCreateByEmail(request.getEmail());
        userService.markEmailVerified(user.getId());

        if (request.getName() != null || request.getMobile() != null) {
            user = userService.updateProfile(user.getId(), request.getName(), request.getMobile());
        }

        // Generate JWT
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .emailVerified(true)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }
}
