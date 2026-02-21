package com.blogapp.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for adding a comment to a blog post")
public class CreateCommentRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Schema(description = "Commenter's name", example = "John Doe")
    private String name;

    @Schema(description = "Commenter's email (optional)", example = "john@example.com")
    private String email;

    @NotBlank(message = "Comment text is required")
    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    @Schema(description = "Comment text", example = "Great article! Very helpful.")
    private String commentText;

    @Schema(description = "Honeypot field — leave empty (anti-spam)", hidden = true)
    private String website; // honeypot field
}
