package com.blogapp.submission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Submission operation response")
public class SubmissionResponse {

    @Schema(description = "Status message")
    private String message;

    @Schema(description = "Submission reference ID (after finish)")
    private String referenceId;

    @Schema(description = "Current step: START, VERIFY, FINISH")
    private String step;

    @Schema(description = "Email associated with submission")
    private String email;
}
