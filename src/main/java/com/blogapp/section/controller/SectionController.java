package com.blogapp.section.controller;

import com.blogapp.section.dto.response.SectionResponse;
import com.blogapp.section.dto.response.SubsectionResponse;
import com.blogapp.section.service.SectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
@Tag(name = "Sections", description = "Public endpoints for browsing sections and subsections")
public class SectionController {

    private final SectionService sectionService;

    @GetMapping
    @Operation(summary = "Get all sections", description = "Returns all sections with their subsections, ordered by sortOrder")
    public ResponseEntity<List<SectionResponse>> getAllSections() {
        return ResponseEntity.ok(sectionService.getAllSections());
    }

    @GetMapping("/{sectionSlug}")
    @Operation(summary = "Get section by slug", description = "Returns a single section with its subsections")
    public ResponseEntity<SectionResponse> getSectionBySlug(
            @Parameter(description = "Section slug", example = "literature") @PathVariable String sectionSlug) {
        return ResponseEntity.ok(sectionService.getSectionBySlug(sectionSlug));
    }

    @GetMapping("/{sectionSlug}/subsections")
    @Operation(summary = "Get subsections", description = "Returns all subsections for a given section")
    public ResponseEntity<List<SubsectionResponse>> getSubsections(
            @Parameter(description = "Section slug", example = "literature") @PathVariable String sectionSlug) {
        return ResponseEntity.ok(sectionService.getSubsectionsBySectionSlug(sectionSlug));
    }
}
