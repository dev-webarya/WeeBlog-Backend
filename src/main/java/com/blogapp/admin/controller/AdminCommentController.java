package com.blogapp.admin.controller;

import com.blogapp.comment.dto.response.CommentResponse;
import com.blogapp.comment.service.CommentService;
import com.blogapp.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
@Tag(name = "Admin - Comments Moderation", description = "Admin endpoints for comment moderation")
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "Get all comments", description = "List all comments across the entire platform")
    public ResponseEntity<PageResponse<CommentResponse>> getAllComments(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(commentService.getPendingComments(page, size));
    }

    @PostMapping("/{id}/hide")
    @Operation(summary = "Hide a comment", description = "Hide an abusive or inappropriate comment from public view")
    public ResponseEntity<Map<String, String>> hideComment(
            @Parameter(description = "Comment ID") @PathVariable String id) {

        commentService.hideComment(id);
        return ResponseEntity.ok(Map.of("message", "Comment hidden successfully"));
    }

    @PostMapping("/{id}/unhide")
    @Operation(summary = "Unhide a comment", description = "Restore a hidden comment to public view")
    public ResponseEntity<Map<String, String>> unhideComment(
            @Parameter(description = "Comment ID") @PathVariable String id) {

        commentService.unhideComment(id);
        return ResponseEntity.ok(Map.of("message", "Comment unhidden successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a comment", description = "Permanently delete a comment")
    public ResponseEntity<Map<String, String>> deleteComment(
            @Parameter(description = "Comment ID") @PathVariable String id) {

        commentService.deleteComment(id);
        return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));
    }
}
