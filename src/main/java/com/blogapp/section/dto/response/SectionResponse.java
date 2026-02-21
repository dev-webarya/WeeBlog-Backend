package com.blogapp.section.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Section with its subsections")
public class SectionResponse {

    @Schema(description = "Section ID")
    private String id;

    @Schema(description = "Section name", example = "Literature")
    private String name;

    @Schema(description = "URL-friendly slug", example = "literature")
    private String slug;

    @Schema(description = "Display order")
    private int sortOrder;

    @Schema(description = "Subsections within this section")
    private List<SubsectionResponse> subsections;
}
