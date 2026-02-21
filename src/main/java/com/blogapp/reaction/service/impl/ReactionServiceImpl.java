package com.blogapp.reaction.service.impl;

import com.blogapp.blog.entity.BlogPost;
import com.blogapp.blog.repository.BlogPostRepository;
import com.blogapp.common.exception.RateLimitException;
import com.blogapp.common.exception.ResourceNotFoundException;
import com.blogapp.reaction.dto.request.ReactionRequest;
import com.blogapp.reaction.dto.response.ReactionResponse;
import com.blogapp.reaction.entity.BlogReaction;
import com.blogapp.reaction.enums.ReactionType;
import com.blogapp.reaction.mapper.ReactionMapper;
import com.blogapp.reaction.repository.ReactionRepository;
import com.blogapp.reaction.service.ReactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final BlogPostRepository blogPostRepository;
    private final ReactionMapper reactionMapper;

    @Value("${blog.rate-limit.reactions-per-minute:10}")
    private int reactionsPerMinute;

    @Override
    public ReactionResponse toggleReaction(String blogId, ReactionRequest request, String ipAddress) {
        // Verify blog exists
        BlogPost blog = blogPostRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog", "id", blogId));

        String visitorKey = request.getVisitorKey();

        // Rate limit check
        long recentReactions = reactionRepository.countByVisitorKeyAndCreatedAtAfter(
                visitorKey, LocalDateTime.now().minusMinutes(1));
        if (recentReactions >= reactionsPerMinute) {
            throw new RateLimitException("Too many reactions. Please wait before trying again.");
        }

        Optional<BlogReaction> existingReaction = reactionRepository.findByBlogIdAndVisitorKey(blogId, visitorKey);
        String action;
        ReactionType currentReaction;

        if (existingReaction.isPresent()) {
            BlogReaction reaction = existingReaction.get();
            if (reaction.getReactionType() == request.getReactionType()) {
                // Same reaction → remove it (toggle off)
                reactionRepository.delete(reaction);
                action = "REMOVED";
                currentReaction = null;

                // Update blog counters
                if (request.getReactionType() == ReactionType.LIKE) {
                    blog.setLikesCount(Math.max(0, blog.getLikesCount() - 1));
                } else {
                    blog.setDislikesCount(Math.max(0, blog.getDislikesCount() - 1));
                }
            } else {
                // Different reaction → switch
                ReactionType oldType = reaction.getReactionType();
                reaction.setReactionType(request.getReactionType());
                reactionRepository.save(reaction);
                action = "SWITCHED";
                currentReaction = request.getReactionType();

                // Update blog counters — decrement old, increment new
                if (oldType == ReactionType.LIKE) {
                    blog.setLikesCount(Math.max(0, blog.getLikesCount() - 1));
                    blog.setDislikesCount(blog.getDislikesCount() + 1);
                } else {
                    blog.setDislikesCount(Math.max(0, blog.getDislikesCount() - 1));
                    blog.setLikesCount(blog.getLikesCount() + 1);
                }
            }
        } else {
            // New reaction
            String ipHash = hashIp(ipAddress);
            BlogReaction newReaction = BlogReaction.builder()
                    .blogId(blogId)
                    .reactionType(request.getReactionType())
                    .visitorKey(visitorKey)
                    .ipHash(ipHash)
                    .build();
            reactionRepository.save(newReaction);
            action = "ADDED";
            currentReaction = request.getReactionType();

            // Update blog counters
            if (request.getReactionType() == ReactionType.LIKE) {
                blog.setLikesCount(blog.getLikesCount() + 1);
            } else {
                blog.setDislikesCount(blog.getDislikesCount() + 1);
            }
        }

        blogPostRepository.save(blog);

        return reactionMapper.toResponse(blogId, blog.getLikesCount(), blog.getDislikesCount(),
                currentReaction, action);
    }

    @Override
    public ReactionResponse getReactionStatus(String blogId, String visitorKey) {
        BlogPost blog = blogPostRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog", "id", blogId));

        ReactionType userReaction = null;
        if (visitorKey != null) {
            Optional<BlogReaction> reaction = reactionRepository.findByBlogIdAndVisitorKey(blogId, visitorKey);
            userReaction = reaction.map(BlogReaction::getReactionType).orElse(null);
        }

        return reactionMapper.toResponse(blogId, blog.getLikesCount(), blog.getDislikesCount(),
                userReaction, null);
    }

    private String hashIp(String ip) {
        if (ip == null)
            return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ip.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return ip;
        }
    }
}
