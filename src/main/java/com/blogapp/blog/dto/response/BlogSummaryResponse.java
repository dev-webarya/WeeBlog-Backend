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
@Schema(description = "Summary view of a blog post (for listing pages)")
public class BlogSummaryResponse {

    @Schema(description = "Blog ID")
    private String id;

    @Schema(description = "Blog title")
    private String title;

    @Schema(description = "URL-friendly slug")
    private String slug;

    @Schema(description = "Short excerpt")
    private String excerpt;

    @Schema(description = "Featured image URL")
    private String featuredImageUrl;

    @Schema(description = "Section name")
    private String sectionName;

    @Schema(description = "Section slug")
    private String sectionSlug;

    @Schema(description = "Subsection name")
    private String subsectionName;

    @Schema(description = "Subsection slug")
    private String subsectionSlug;

    @Schema(description = "Author name")
    private String authorName;

    @Schema(description = "Blog status")
    private BlogStatus status;

    @Schema(description = "Published date")
    private LocalDateTime publishedAt;

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

    @Schema(description = "Whether this blog is premium-gated")
    private boolean premium;
}
