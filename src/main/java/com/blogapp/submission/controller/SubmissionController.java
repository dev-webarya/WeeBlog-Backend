package com.blogapp.submission.controller;

import com.blogapp.submission.dto.request.SubmissionFinishRequest;
import com.blogapp.submission.dto.request.SubmissionStartRequest;
import com.blogapp.submission.dto.request.SubmissionVerifyRequest;
import com.blogapp.submission.dto.response.SubmissionResponse;
import com.blogapp.submission.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/blogs/submission")
@RequiredArgsConstructor
@Tag(name = "Blog Submission", description = "3-step blog submission flow: start → verify OTP → finish")
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/start")
    @Operation(summary = "Step 1: Start submission", description = "Submit blog content + author identity. Sends OTP to author's email.")
    public ResponseEntity<SubmissionResponse> startSubmission(
            @Valid @RequestBody SubmissionStartRequest request) {

        return ResponseEntity.ok(submissionService.startSubmission(request));
    }

    @PostMapping("/verify")
    @Operation(summary = "Step 2: Verify OTP", description = "Verify the OTP sent to author's email")
    public ResponseEntity<SubmissionResponse> verifySubmission(
            @Valid @RequestBody SubmissionVerifyRequest request) {

        return ResponseEntity.ok(submissionService.verifySubmission(request));
    }

    @PostMapping("/finish")
    @Operation(summary = "Step 3: Finish submission", description = "Finalize the blog submission after OTP verification. Blog goes to PENDING approval.")
    public ResponseEntity<SubmissionResponse> finishSubmission(
            @Valid @RequestBody SubmissionFinishRequest request) {

        return ResponseEntity.ok(submissionService.finishSubmission(request));
    }
}
