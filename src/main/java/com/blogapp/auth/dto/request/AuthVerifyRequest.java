package com.blogapp.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to verify OTP and receive a JWT token")
public class AuthVerifyRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")
    @Schema(example = "reader@example.com")
    private String email;

    @NotBlank(message = "OTP is required")
    @Schema(example = "482910")
    private String otp;

    @Schema(description = "Optional name for new users", example = "Rohit Gupta")
    private String name;

    @Schema(description = "Optional mobile number", example = "+919876543210")
    private String mobile;
}
