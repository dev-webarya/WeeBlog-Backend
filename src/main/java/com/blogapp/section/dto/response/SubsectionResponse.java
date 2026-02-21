package com.blogapp.section.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Subsection detail")
public class SubsectionResponse {

    @Schema(description = "Subsection ID")
    private String id;

    @Schema(description = "Parent section ID")
    private String sectionId;

    @Schema(description = "Subsection name", example = "Stories")
    private String name;

    @Schema(description = "URL-friendly slug", example = "stories")
    private String slug;

    @Schema(description = "Optional description")
    private String description;

    @Schema(description = "Display order")
    private int sortOrder;
}
