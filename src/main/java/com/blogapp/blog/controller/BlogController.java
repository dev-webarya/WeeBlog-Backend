package com.blogapp.blog.controller;

import com.blogapp.blog.dto.response.ArchiveResponse;
import com.blogapp.blog.dto.response.BlogDetailResponse;
import com.blogapp.blog.dto.response.BlogSummaryResponse;
import com.blogapp.blog.service.BlogService;
import com.blogapp.common.dto.PageResponse;
import com.blogapp.entitlement.service.EntitlementService;
import com.blogapp.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@Tag(name = "Blog", description = "Public blog APIs — listing, detail, archive")
public class BlogController {

    private final BlogService blogService;
    private final EntitlementService entitlementService;

    @GetMapping
    @Operation(summary = "Get published blogs", description = "Fetch published blogs with optional search, section, year/month filter, and sorting")
    public ResponseEntity<PageResponse<BlogSummaryResponse>> getPublishedBlogs(
            @Parameter(description = "Search keyword (searches title + excerpt)") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by section ID") @RequestParam(required = false) String sectionId,
            @Parameter(description = "Filter by subsection ID") @RequestParam(required = false) String subsectionId,
            @Parameter(description = "Filter by year (e.g., 2026)") @RequestParam(required = false) Integer year,
            @Parameter(description = "Filter by month (1–12)") @RequestParam(required = false) Integer month,
            @Parameter(description = "Sort order: recent (default), popular, oldest, most_commented") @RequestParam(required = false, defaultValue = "recent") String sort,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity
                .ok(blogService.getPublishedBlogs(search, year, month, sectionId, subsectionId, sort, page, size));
    }

    @GetMapping("/archive")
    @Operation(summary = "Get archive index", description = "Returns year → month breakdown with blog counts for the sidebar archive index")
    public ResponseEntity<List<ArchiveResponse>> getArchive() {
        return ResponseEntity.ok(blogService.getArchive());
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get blog by slug", description = "Fetch full blog detail. For premium blogs, part2 content is only returned if the user has an active entitlement.")
    public ResponseEntity<BlogDetailResponse> getBlogBySlug(
            @Parameter(description = "Blog slug", example = "how-to-prepare-for-igcse-physics") @PathVariable String slug) {

        BlogDetailResponse blog = blogService.getBlogBySlug(slug);

        // Increment view count asynchronously (fire-and-forget)
        blogService.incrementViewCount(blog.getId());

        // Paywall gating for premium blogs
        if (blog.isPremium()) {
            String userId = getAuthenticatedUserId();
            boolean hasEntitlement = false;

            if (userId != null) {
                hasEntitlement = entitlementService.hasAccess(
                        userId, blog.getId(), blog.getSectionId(), blog.getSubsectionId());
            }

            blog.setHasEntitlement(hasEntitlement);

            if (!hasEntitlement) {
                // Hide premium content — only serve part 1
                blog.setContentHtml(blog.getContentPart1Html());
                blog.setContentPart2Html(null);
                blog.setContentJson(null);
            }
        } else {
            // Non-premium: full access
            blog.setHasEntitlement(true);
        }

        return ResponseEntity.ok(blog);
    }

    /**
     * Extract the authenticated user ID from SecurityContext, or null if anonymous.
     */
    private String getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User user) {
            return user.getId();
        }
        return null;
    }
}
