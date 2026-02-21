package com.blogapp.section.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create or update a subsection")
public class SubsectionRequest {

    @NotBlank(message = "Subsection name is required")
    @Size(max = 100, message = "Subsection name must not exceed 100 characters")
    @Schema(description = "Subsection name", example = "Stories")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Optional description", example = "Short stories, epics, and narrative fiction")
    private String description;

    @Schema(description = "Display order (lower = first)", example = "1")
    private Integer sortOrder;
}
