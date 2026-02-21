package com.blogapp.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Verify a Razorpay payment after checkout")
public class VerifyPaymentRequest {

    @NotBlank
    @Schema(description = "Razorpay order ID", example = "order_L3h4kqjr2W8sBm")
    private String razorpayOrderId;

    @NotBlank
    @Schema(description = "Razorpay payment ID", example = "pay_L3h4mn0R2W8sBm")
    private String razorpayPaymentId;

    @NotBlank
    @Schema(description = "Razorpay payment signature for verification")
    private String razorpaySignature;
}
