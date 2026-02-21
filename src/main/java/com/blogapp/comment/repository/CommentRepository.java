package com.blogapp.comment.repository;

import com.blogapp.comment.entity.BlogComment;
import com.blogapp.comment.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CommentRepository extends MongoRepository<BlogComment, String> {

    Page<BlogComment> findByBlogIdAndStatus(String blogId, CommentStatus status, Pageable pageable);

    Page<BlogComment> findByBlogId(String blogId, Pageable pageable);

    Page<BlogComment> findByStatus(CommentStatus status, Pageable pageable);

    long countByBlogIdAndStatus(String blogId, CommentStatus status);

    long countByIpHashAndCreatedAtAfter(String ipHash, LocalDateTime after);
}
