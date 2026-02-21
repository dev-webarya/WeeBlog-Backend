package com.blogapp.reaction.mapper;

import com.blogapp.reaction.dto.response.ReactionResponse;
import com.blogapp.reaction.enums.ReactionType;
import org.springframework.stereotype.Component;

@Component
public class ReactionMapper {

    public ReactionResponse toResponse(String blogId, long likesCount, long dislikesCount,
            ReactionType userReaction, String action) {
        return ReactionResponse.builder()
                .blogId(blogId)
                .likesCount(likesCount)
                .dislikesCount(dislikesCount)
                .userReaction(userReaction)
                .action(action)
                .build();
    }
}
