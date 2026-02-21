package com.blogapp.reaction.dto.request;

import com.blogapp.reaction.enums.ReactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Toggle a reaction (like/dislike) on a blog post")
public class ReactionRequest {

    @NotNull(message = "Reaction type is required")
    @Schema(description = "LIKE or DISLIKE", example = "LIKE")
    private ReactionType reactionType;

    @Schema(description = "Visitor key from cookie/fingerprint", example = "visitor-abc-123")
    private String visitorKey;
}
