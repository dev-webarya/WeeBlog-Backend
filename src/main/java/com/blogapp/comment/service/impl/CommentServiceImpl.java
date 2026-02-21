package com.blogapp.comment.service.impl;

import com.blogapp.blog.entity.BlogPost;
import com.blogapp.blog.repository.BlogPostRepository;
import com.blogapp.comment.dto.request.CreateCommentRequest;
import com.blogapp.comment.dto.response.CommentResponse;
import com.blogapp.comment.entity.BlogComment;
import com.blogapp.comment.enums.CommentStatus;
import com.blogapp.comment.mapper.CommentMapper;
import com.blogapp.comment.repository.CommentRepository;
import com.blogapp.comment.service.CommentService;
import com.blogapp.common.dto.PageResponse;
import com.blogapp.common.exception.BadRequestException;
import com.blogapp.common.exception.RateLimitException;
import com.blogapp.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BlogPostRepository blogPostRepository;
    private final CommentMapper commentMapper;

    @Value("${blog.rate-limit.comments-per-minute:5}")
    private int commentsPerMinute;

    @Override
    public PageResponse<CommentResponse> getCommentsByBlogId(String blogId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<BlogComment> commentPage = commentRepository.findByBlogIdAndStatus(
                blogId, CommentStatus.VISIBLE, pageable);

        List<CommentResponse> content = commentPage.getContent().stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<CommentResponse>builder()
                .content(content)
                .page(commentPage.getNumber())
                .size(commentPage.getSize())
                .totalElements(commentPage.getTotalElements())
                .totalPages(commentPage.getTotalPages())
                .first(commentPage.isFirst())
                .last(commentPage.isLast())
                .build();
    }

    @Override
    public CommentResponse addComment(String blogId, CreateCommentRequest request, String ipAddress) {
        // Verify blog exists and is published
        BlogPost blog = blogPostRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog", "id", blogId));

        // Honeypot check — if the hidden "website" field is filled, it's a bot
        if (request.getWebsite() != null && !request.getWebsite().isBlank()) {
            log.warn("Honeypot triggered for blog {} from IP {}", blogId, ipAddress);
            throw new BadRequestException("Spam detected");
        }

        // Rate limit check
        String ipHash = hashIp(ipAddress);
        long recentComments = commentRepository.countByIpHashAndCreatedAtAfter(
                ipHash, LocalDateTime.now().minusMinutes(1));
        if (recentComments >= commentsPerMinute) {
            throw new RateLimitException("Too many comments. Please wait before posting again.");
        }

        BlogComment comment = commentMapper.toEntity(blogId, request, ipHash);
        comment = commentRepository.save(comment);

        // Increment comment count on blog
        blog.setCommentsCount(blog.getCommentsCount() + 1);
        blogPostRepository.save(blog);

        log.info("Comment added to blog {} by {}", blogId, request.getName());
        return commentMapper.toResponse(comment);
    }

    @Override
    public void hideComment(String commentId) {
        BlogComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        comment.setStatus(CommentStatus.HIDDEN);
        commentRepository.save(comment);

        // Decrement blog comment count
        BlogPost blog = blogPostRepository.findById(comment.getBlogId()).orElse(null);
        if (blog != null) {
            blog.setCommentsCount(Math.max(0, blog.getCommentsCount() - 1));
            blogPostRepository.save(blog);
        }

        log.info("Comment hidden: {}", commentId);
    }

    @Override
    public void unhideComment(String commentId) {
        BlogComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        comment.setStatus(CommentStatus.VISIBLE);
        commentRepository.save(comment);

        // Increment blog comment count
        BlogPost blog = blogPostRepository.findById(comment.getBlogId()).orElse(null);
        if (blog != null) {
            blog.setCommentsCount(blog.getCommentsCount() + 1);
            blogPostRepository.save(blog);
        }

        log.info("Comment unhidden: {}", commentId);
    }

    @Override
    public void deleteComment(String commentId) {
        BlogComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        commentRepository.delete(comment);

        // Decrement blog comment count
        if (comment.getStatus() == CommentStatus.VISIBLE) {
            BlogPost blog = blogPostRepository.findById(comment.getBlogId()).orElse(null);
            if (blog != null) {
                blog.setCommentsCount(Math.max(0, blog.getCommentsCount() - 1));
                blogPostRepository.save(blog);
            }
        }

        log.info("Comment deleted: {}", commentId);
    }

    @Override
    public PageResponse<CommentResponse> getPendingComments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<BlogComment> commentPage = commentRepository.findAll(pageable);

        List<CommentResponse> content = commentPage.getContent().stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<CommentResponse>builder()
                .content(content)
                .page(commentPage.getNumber())
                .size(commentPage.getSize())
                .totalElements(commentPage.getTotalElements())
                .totalPages(commentPage.getTotalPages())
                .first(commentPage.isFirst())
                .last(commentPage.isLast())
                .build();
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
