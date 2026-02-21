package com.blogapp.comment.mapper;

import com.blogapp.comment.dto.request.CreateCommentRequest;
import com.blogapp.comment.dto.response.CommentResponse;
import com.blogapp.comment.entity.BlogComment;
import com.blogapp.comment.enums.CommentStatus;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public BlogComment toEntity(String blogId, CreateCommentRequest request, String ipHash) {
        return BlogComment.builder()
                .blogId(blogId)
                .name(request.getName())
                .email(request.getEmail())
                .commentText(request.getCommentText())
                .status(CommentStatus.VISIBLE) // auto-publish
                .ipHash(ipHash)
                .build();
    }

    public CommentResponse toResponse(BlogComment entity) {
        return CommentResponse.builder()
                .id(entity.getId())
                .blogId(entity.getBlogId())
                .name(entity.getName())
                .commentText(entity.getCommentText())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
