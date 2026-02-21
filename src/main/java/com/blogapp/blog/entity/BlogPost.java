package com.blogapp.blog.entity;

import com.blogapp.blog.enums.BlogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "blog_posts")
public class BlogPost {

    @Id
    private String id;

    @TextIndexed(weight = 3)
    private String title;

    @Indexed(unique = true)
    private String slug;

    @TextIndexed(weight = 2)
    private String excerpt;

    private String contentHtml;

    private String contentPart1Html; // First ~50% of content (free)

    private String contentPart2Html; // Second ~50% of content (premium-gated)

    private String contentJson; // optional — for editors that output JSON (e.g., TipTap)

    private String featuredImageUrl;

    // Taxonomy
    @Indexed
    private String sectionId;

    @Indexed
    private String subsectionId;

    private String authorName;

    @Indexed
    private String authorEmail;

    private String authorMobile;

    @Indexed
    private String authorUserId; // links to User entity (null for pre-auth submissions)

    // Admin quality rating (1-10), drives paywall gating
    private Integer internalRating;

    @Indexed
    @Builder.Default
    private BlogStatus status = BlogStatus.DRAFT;

    private LocalDateTime submittedAt;

    @Indexed
    private LocalDateTime publishedAt;

    private String approvedByAdminId;

    private String rejectionReason;

    // Derived fields for fast archive queries
    @Indexed
    private Integer year;

    @Indexed
    private Integer month;

    private List<String> tags;

    @Builder.Default
    private long viewsCount = 0;

    @Builder.Default
    private long likesCount = 0;

    @Builder.Default
    private long dislikesCount = 0;

    @Builder.Default
    private long commentsCount = 0;

    @Builder.Default
    private boolean emailSent = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
