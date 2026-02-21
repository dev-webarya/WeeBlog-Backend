package com.blogapp.reaction.controller;

import com.blogapp.reaction.dto.request.ReactionRequest;
import com.blogapp.reaction.dto.response.ReactionResponse;
import com.blogapp.reaction.service.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blogs/{blogId}/reaction")
@RequiredArgsConstructor
@Tag(name = "Reaction", description = "Like/Dislike toggle for blog posts")
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping
    @Operation(summary = "Toggle reaction", description = "Toggle like/dislike on a blog post. If same reaction exists → removes it. If different → switches.")
    public ResponseEntity<ReactionResponse> toggleReaction(
            @Parameter(description = "Blog ID") @PathVariable String blogId,
            @Valid @RequestBody ReactionRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(reactionService.toggleReaction(blogId, request, ipAddress));
    }

    @GetMapping
    @Operation(summary = "Get reaction status", description = "Get current like/dislike counts and the visitor's reaction")
    public ResponseEntity<ReactionResponse> getReactionStatus(
            @Parameter(description = "Blog ID") @PathVariable String blogId,
            @Parameter(description = "Visitor key from cookie") @RequestParam(required = false) String visitorKey) {

        return ResponseEntity.ok(reactionService.getReactionStatus(blogId, visitorKey));
    }
}
