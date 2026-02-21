package com.blogapp.subscriber.dto.response;

import com.blogapp.subscriber.enums.SubscriberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Subscriber details")
public class SubscriberResponse {

    @Schema(description = "Subscriber ID")
    private String id;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Name")
    private String name;

    @Schema(description = "Status")
    private SubscriberStatus status;

    @Schema(description = "Is email verified")
    private boolean verified;

    @Schema(description = "Subscribed date")
    private LocalDateTime createdAt;
}
