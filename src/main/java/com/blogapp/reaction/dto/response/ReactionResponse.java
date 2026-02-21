package com.blogapp.reaction.dto.response;

import com.blogapp.reaction.enums.ReactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reaction status for a blog post")
public class ReactionResponse {

    @Schema(description = "Blog ID")
    private String blogId;

    @Schema(description = "Total likes")
    private long likesCount;

    @Schema(description = "Total dislikes")
    private long dislikesCount;

    @Schema(description = "Current visitor's reaction (null if none)")
    private ReactionType userReaction;

    @Schema(description = "Action performed: ADDED, SWITCHED, REMOVED")
    private String action;
}
