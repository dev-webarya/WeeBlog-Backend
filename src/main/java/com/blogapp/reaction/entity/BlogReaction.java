package com.blogapp.reaction.entity;

import com.blogapp.reaction.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "blog_reactions")
@CompoundIndex(name = "blog_visitor_idx", def = "{'blogId': 1, 'visitorKey': 1}", unique = true)
public class BlogReaction {

    @Id
    private String id;

    @Indexed
    private String blogId;

    private ReactionType reactionType;

    private String visitorKey; // cookie/fingerprint token

    private String ipHash; // optional — for throttling

    @CreatedDate
    private LocalDateTime createdAt;
}
