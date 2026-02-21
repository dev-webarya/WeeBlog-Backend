package com.blogapp.blog.dto.response;

import com.blogapp.blog.enums.BlogStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Full detail view of a blog post")
public class BlogDetailResponse {

    @Schema(description = "Blog ID")
    private String id;

    @Schema(description = "Blog title")
    private String title;

    @Schema(description = "URL-friendly slug")
    private String slug;

    @Schema(description = "Short excerpt")
    private String excerpt;

    @Schema(description = "Sanitized HTML content")
    private String contentHtml;

    @Schema(description = "JSON content (if available)")
    private String contentJson;

    @Schema(description = "Featured image URL")
    private String featuredImageUrl;

    // Taxonomy
    @Schema(description = "Section ID")
    private String sectionId;

    @Schema(description = "Section name")
    private String sectionName;

    @Schema(description = "Section slug")
    private String sectionSlug;

    @Schema(description = "Subsection ID")
    private String subsectionId;

    @Schema(description = "Subsection name")
    private String subsectionName;

    @Schema(description = "Subsection slug")
    private String subsectionSlug;

    @Schema(description = "Author name")
    private String authorName;

    @Schema(description = "Author email")
    private String authorEmail;

    @Schema(description = "Blog status")
    private BlogStatus status;

    @Schema(description = "Submitted date")
    private LocalDateTime submittedAt;

    @Schema(description = "Published date")
    private LocalDateTime publishedAt;

    @Schema(description = "Rejection reason (if rejected)")
    private String rejectionReason;

    @Schema(description = "Tags")
    private List<String> tags;

    @Schema(description = "Total likes")
    private long likesCount;

    @Schema(description = "Total dislikes")
    private long dislikesCount;

    @Schema(description = "Total comments")
    private long commentsCount;

    @Schema(description = "Total views")
    private long viewsCount;

    @Schema(description = "Created date")
    private LocalDateTime createdAt;

    // Paywall fields
    @Schema(description = "Internal quality rating (1-10), admin only")
    private Integer internalRating;

    @Schema(description = "Whether this blog is premium-gated (rating > 6)")
    private boolean premium;

    @Schema(description = "Free preview content (first ~50%)")
    private String contentPart1Html;

    @Schema(description = "Premium content (second ~50%) — null if user is not entitled")
    private String contentPart2Html;

    @Schema(description = "Whether the current user has entitlement to view premium content")
    private boolean hasEntitlement;
}
