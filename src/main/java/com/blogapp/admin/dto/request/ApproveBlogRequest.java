package com.blogapp.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Approve a blog post — includes optional internal quality rating")
public class ApproveBlogRequest {

    @Schema(description = "Admin user ID performing the approval", example = "admin-001")
    private String adminId;

    @Min(1)
    @Max(10)
    @Schema(description = "Internal quality rating (1-10). Blogs rated > 6 become premium-gated.", example = "7")
    private Integer internalRating;

    @Schema(description = "Assign section ID during approval")
    private String sectionId;

    @Schema(description = "Assign subsection ID during approval")
    private String subsectionId;
}
