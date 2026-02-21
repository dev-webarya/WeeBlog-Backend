package com.blogapp.submission.service.impl;

import com.blogapp.blog.dto.request.CreateBlogRequest;
import com.blogapp.blog.entity.BlogPost;
import com.blogapp.blog.service.BlogService;
import com.blogapp.common.exception.BadRequestException;
import com.blogapp.otp.enums.OtpPurpose;
import com.blogapp.otp.service.EmailService;
import com.blogapp.otp.service.OtpService;
import com.blogapp.submission.dto.request.SubmissionFinishRequest;
import com.blogapp.submission.dto.request.SubmissionStartRequest;
import com.blogapp.submission.dto.request.SubmissionVerifyRequest;
import com.blogapp.submission.dto.response.SubmissionResponse;
import com.blogapp.submission.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final OtpService otpService;
    private final BlogService blogService;
    private final EmailService emailService;

    // Temporary storage for draft submissions awaiting OTP verification
    // In production, consider using Redis or a database-backed session store
    private final Map<String, SubmissionStartRequest> pendingSubmissions = new ConcurrentHashMap<>();

    @Override
    public SubmissionResponse startSubmission(SubmissionStartRequest request) {
        // Store the draft submission temporarily (keyed by email)
        pendingSubmissions.put(request.getAuthorEmail(), request);

        // Send OTP to author's email
        otpService.sendOtp(request.getAuthorEmail(), OtpPurpose.BLOG_SUBMISSION);

        log.info("Blog submission started. OTP sent to: {}", request.getAuthorEmail());

        return SubmissionResponse.builder()
                .message("OTP has been sent to your email. Please verify to complete submission.")
                .step("START")
                .email(request.getAuthorEmail())
                .build();
    }

    @Override
    public SubmissionResponse verifySubmission(SubmissionVerifyRequest request) {
        // Verify OTP
        otpService.verifyOtp(request.getEmail(), request.getOtp(), OtpPurpose.BLOG_SUBMISSION);

        log.info("Blog submission OTP verified for: {}", request.getEmail());

        return SubmissionResponse.builder()
                .message("OTP verified successfully. Please call finish endpoint to complete submission.")
                .step("VERIFY")
                .email(request.getEmail())
                .build();
    }

    @Override
    public SubmissionResponse finishSubmission(SubmissionFinishRequest request) {
        // Check if email is verified
        if (!otpService.isEmailVerified(request.getEmail(), OtpPurpose.BLOG_SUBMISSION)) {
            throw new BadRequestException("Email not verified. Please complete OTP verification first.");
        }

        // Retrieve the pending submission
        SubmissionStartRequest draft = pendingSubmissions.get(request.getEmail());
        if (draft == null) {
            throw new BadRequestException(
                    "No pending submission found for this email. Please start the submission process again.");
        }

        // Create the blog post via BlogService
        CreateBlogRequest blogRequest = CreateBlogRequest.builder()
                .title(draft.getTitle())
                .excerpt(draft.getExcerpt())
                .contentHtml(draft.getContentHtml())
                .contentJson(draft.getContentJson())
                .featuredImageUrl(draft.getFeaturedImageUrl())
                .tags(draft.getTags())
                .build();

        BlogPost savedBlog = blogService.createBlog(
                blogRequest,
                draft.getAuthorName(),
                draft.getAuthorEmail(),
                draft.getAuthorMobile());

        // Remove from pending submissions
        pendingSubmissions.remove(request.getEmail());

        // Send confirmation email
        emailService.sendSubmissionReceivedEmail(
                draft.getAuthorEmail(),
                draft.getTitle(),
                savedBlog.getId());

        log.info("Blog submission completed. Blog ID: {}, Author: {}",
                savedBlog.getId(), draft.getAuthorEmail());

        return SubmissionResponse.builder()
                .message("Thanks! Your blog is submitted and awaiting admin approval.")
                .step("FINISH")
                .referenceId(savedBlog.getId())
                .email(request.getEmail())
                .build();
    }
}
