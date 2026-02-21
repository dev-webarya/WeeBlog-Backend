package com.blogapp.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response after successful OTP verification — contains the JWT and user info")
public class AuthResponse {

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(example = "Bearer")
    private String tokenType;

    private UserInfo user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        @Schema(example = "6613f7a2b1d4c20e9a3b5c7d")
        private String id;

        @Schema(example = "reader@example.com")
        private String email;

        @Schema(example = "Rohit Gupta")
        private String name;

        @Schema(example = "true")
        private boolean emailVerified;
    }
}
