package com.blogapp.admin.dto.request;

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
@Schema(description = "Reject a blog post — reason is required")
public class RejectBlogRequest {

    @NotBlank(message = "Rejection reason is required")
    @Schema(description = "Reason for rejecting the blog", example = "Content does not meet quality guidelines")
    private String reason;
}
