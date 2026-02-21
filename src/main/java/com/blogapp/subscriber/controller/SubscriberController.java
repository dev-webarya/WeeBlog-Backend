package com.blogapp.subscriber.controller;

import com.blogapp.subscriber.dto.request.SubscribeRequest;
import com.blogapp.subscriber.dto.response.SubscriberResponse;
import com.blogapp.subscriber.service.SubscriberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/blogs/subscribe")
@RequiredArgsConstructor
@Tag(name = "Subscriber", description = "Blog subscription with email OTP verification")
public class SubscriberController {

    private final SubscriberService subscriberService;

    @PostMapping("/start")
    @Operation(summary = "Start subscription", description = "Submit email to subscribe. Sends OTP to the provided email for verification.")
    public ResponseEntity<SubscriberResponse> startSubscription(
            @Valid @RequestBody SubscribeRequest request) {

        return new ResponseEntity<>(subscriberService.startSubscription(request), HttpStatus.OK);
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify subscription OTP", description = "Verify the OTP sent to email to activate subscription")
    public ResponseEntity<SubscriberResponse> verifySubscription(
            @Parameter(description = "Email address") @RequestParam String email,
            @Parameter(description = "6-digit OTP") @RequestParam String otp) {

        return ResponseEntity.ok(subscriberService.verifySubscription(email, otp));
    }

    @PostMapping("/unsubscribe")
    @Operation(summary = "Unsubscribe", description = "Unsubscribe from blog updates")
    public ResponseEntity<Map<String, String>> unsubscribe(
            @Parameter(description = "Email address") @RequestParam String email) {

        subscriberService.unsubscribe(email);
        return ResponseEntity.ok(Map.of("message", "Successfully unsubscribed"));
    }
}
