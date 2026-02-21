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
@Schema(description = "Create a checkout order for content access")
public class CheckoutRequest {

    @NotBlank(message = "Plan type is required")
    @Schema(description = "Plan type: PER_BLOG, SUBSCRIPTION_SUBSECTION, SUBSCRIPTION_SECTION, SUBSCRIPTION_ALL", example = "PER_BLOG")
    private String planType;

    @Schema(description = "Duration for subscriptions: 1M, 3M, 6M, 12M. Not required for PER_BLOG.", example = "1M")
    private String duration;

    @Schema(description = "Scope ID: sectionId or subsectionId for subscriptions. Not required for PER_BLOG or ALL.", example = "section-001")
    private String scopeId;

    @Schema(description = "Blog ID for PER_BLOG purchases. Not required for subscriptions.", example = "6613f7a2b1d4c20e9a3b5c7d")
    private String blogId;
}
