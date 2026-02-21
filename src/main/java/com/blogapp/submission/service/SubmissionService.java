package com.blogapp.submission.service;

import com.blogapp.submission.dto.request.SubmissionFinishRequest;
import com.blogapp.submission.dto.request.SubmissionStartRequest;
import com.blogapp.submission.dto.request.SubmissionVerifyRequest;
import com.blogapp.submission.dto.response.SubmissionResponse;

public interface SubmissionService {

    SubmissionResponse startSubmission(SubmissionStartRequest request);

    SubmissionResponse verifySubmission(SubmissionVerifyRequest request);

    SubmissionResponse finishSubmission(SubmissionFinishRequest request);
}
