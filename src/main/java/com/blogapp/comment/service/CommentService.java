package com.blogapp.comment.service;

import com.blogapp.comment.dto.request.CreateCommentRequest;
import com.blogapp.comment.dto.response.CommentResponse;
import com.blogapp.common.dto.PageResponse;

public interface CommentService {

    PageResponse<CommentResponse> getCommentsByBlogId(String blogId, int page, int size);

    CommentResponse addComment(String blogId, CreateCommentRequest request, String ipAddress);

    void hideComment(String commentId);

    void unhideComment(String commentId);

    void deleteComment(String commentId);

    PageResponse<CommentResponse> getPendingComments(int page, int size);
}
