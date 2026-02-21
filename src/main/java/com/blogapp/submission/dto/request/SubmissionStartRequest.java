package com.blogapp.submission.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Start blog submission — provides blog content + author identity, triggers OTP")
public class SubmissionStartRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Schema(description = "Blog title", example = "How to Prepare for IGCSE Physics")
    private String title;

    @Size(max = 500, message = "Excerpt must not exceed 500 characters")
    @Schema(description = "Short summary/excerpt", example = "A comprehensive guide...")
    private String excerpt;

    @NotBlank(message = "Content is required")
    @Schema(description = "Blog content in HTML format")
    private String contentHtml;

    @Schema(description = "Blog content in JSON format (optional)")
    private String contentJson;

    @Schema(description = "Featured image URL")
    private String featuredImageUrl;

    @Schema(description = "Tags", example = "[\"physics\", \"igcse\"]")
    private List<String> tags;

    // Author identity
    @Schema(description = "Author name", example = "John Doe")
    private String authorName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    @Schema(description = "Author email for verification", example = "author@example.com")
    private String authorEmail;

    @NotBlank(message = "Mobile number is required")
    @Schema(description = "Author mobile number", example = "+919876543210")
    private String authorMobile;
}
