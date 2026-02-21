package com.blogapp.blog.repository;

import com.blogapp.blog.entity.BlogPost;
import com.blogapp.blog.enums.BlogStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends MongoRepository<BlogPost, String> {

    Optional<BlogPost> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<BlogPost> findByStatus(BlogStatus status, Pageable pageable);

    Page<BlogPost> findByStatusAndYearAndMonth(BlogStatus status, Integer year, Integer month, Pageable pageable);

    Page<BlogPost> findByStatusAndYear(BlogStatus status, Integer year, Pageable pageable);

    // Text search on title + excerpt (uses MongoDB text index)
    @Query("{ 'status': ?0, '$text': { '$search': ?1 } }")
    Page<BlogPost> searchByText(BlogStatus status, String searchText, Pageable pageable);

    // Search with year/month filter
    @Query("{ 'status': ?0, 'year': ?1, 'month': ?2, '$text': { '$search': ?3 } }")
    Page<BlogPost> searchByTextAndYearMonth(BlogStatus status, int year, int month, String searchText,
            Pageable pageable);

    // Count blogs by year and month for archive index
    List<BlogPost> findByStatusOrderByPublishedAtDesc(BlogStatus status);

    // For archive aggregation — handled in service with MongoTemplate
    long countByStatusAndYearAndMonth(BlogStatus status, int year, int month);

    // Find by status for admin
    Page<BlogPost> findByStatusIn(List<BlogStatus> statuses, Pageable pageable);

    // Find by author email (for submission reference)
    List<BlogPost> findByAuthorEmail(String authorEmail);

    // Find published blogs where notification email hasn't been sent yet
    List<BlogPost> findByStatusAndEmailSent(BlogStatus status, boolean emailSent);
}
