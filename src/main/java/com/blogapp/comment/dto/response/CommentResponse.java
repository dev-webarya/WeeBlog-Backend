package com.blogapp.comment.dto.response;

import com.blogapp.comment.enums.CommentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Comment response")
public class CommentResponse {

    @Schema(description = "Comment ID")
    private String id;

    @Schema(description = "Blog ID")
    private String blogId;

    @Schema(description = "Commenter's name")
    private String name;

    @Schema(description = "Comment text")
    private String commentText;

    @Schema(description = "Comment status")
    private CommentStatus status;

    @Schema(description = "Created date")
    private LocalDateTime createdAt;
}
