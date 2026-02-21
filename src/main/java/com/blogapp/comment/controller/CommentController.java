package com.blogapp.comment.controller;

import com.blogapp.comment.dto.request.CreateCommentRequest;
import com.blogapp.comment.dto.response.CommentResponse;
import com.blogapp.comment.service.CommentService;
import com.blogapp.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blogs/{blogId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "Blog comments — view and post comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "Get comments for a blog post", description = "Returns visible comments for the specified blog, ordered by newest first")
    public ResponseEntity<PageResponse<CommentResponse>> getComments(
            @Parameter(description = "Blog ID") @PathVariable String blogId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(commentService.getCommentsByBlogId(blogId, page, size));
    }

    @PostMapping
    @Operation(summary = "Add a comment", description = "Post a comment on a blog. Includes honeypot anti-spam and rate limiting.")
    public ResponseEntity<CommentResponse> addComment(
            @Parameter(description = "Blog ID") @PathVariable String blogId,
            @Valid @RequestBody CreateCommentRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();
        CommentResponse response = commentService.addComment(blogId, request, ipAddress);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
