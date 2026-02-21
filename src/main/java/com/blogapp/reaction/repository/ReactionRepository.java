package com.blogapp.reaction.repository;

import com.blogapp.reaction.entity.BlogReaction;
import com.blogapp.reaction.enums.ReactionType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReactionRepository extends MongoRepository<BlogReaction, String> {

    Optional<BlogReaction> findByBlogIdAndVisitorKey(String blogId, String visitorKey);

    long countByBlogIdAndReactionType(String blogId, ReactionType reactionType);

    long countByVisitorKeyAndCreatedAtAfter(String visitorKey, LocalDateTime after);

    void deleteByBlogIdAndVisitorKey(String blogId, String visitorKey);
}
