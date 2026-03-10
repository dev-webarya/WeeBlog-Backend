package com.blogapp.admin.dto.request;

import com.blogapp.blog.dto.request.CreateBlogRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for admins editing a blog, including internal rating for premium toggling")
public class AdminEditBlogRequest extends CreateBlogRequest {

    @Min(1)
    @Max(10)
    @Schema(description = "Internal quality rating (1-10). Blogs rated > 6 become premium-gated.", example = "7")
    private Integer internalRating;
}
