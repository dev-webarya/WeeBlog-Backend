package com.blogapp.otp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OTP operation response")
public class OtpResponse {

    @Schema(description = "Status message", example = "OTP sent successfully to user@example.com")
    private String message;

    @Schema(description = "Email to which OTP was sent")
    private String email;

    @Schema(description = "Is OTP verified")
    private boolean verified;
}
