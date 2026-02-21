package com.blogapp.admin.controller;

import com.blogapp.section.dto.request.SectionRequest;
import com.blogapp.section.dto.request.SubsectionRequest;
import com.blogapp.section.dto.response.SectionResponse;
import com.blogapp.section.dto.response.SubsectionResponse;
import com.blogapp.section.service.SectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/sections")
@RequiredArgsConstructor
@Tag(name = "Admin - Sections", description = "Admin endpoints for managing sections and subsections")
public class AdminSectionController {

    private final SectionService sectionService;

    @PostMapping
    @Operation(summary = "Create a section")
    public ResponseEntity<SectionResponse> createSection(@Valid @RequestBody SectionRequest request) {
        return ResponseEntity.ok(sectionService.createSection(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a section")
    public ResponseEntity<SectionResponse> updateSection(
            @Parameter(description = "Section ID") @PathVariable String id,
            @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.ok(sectionService.updateSection(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a section", description = "Deletes a section and all its subsections")
    public ResponseEntity<Map<String, String>> deleteSection(
            @Parameter(description = "Section ID") @PathVariable String id) {
        sectionService.deleteSection(id);
        return ResponseEntity.ok(Map.of("message", "Section deleted successfully"));
    }

    // ---- Subsection endpoints ----

    @PostMapping("/{sectionId}/subsections")
    @Operation(summary = "Create a subsection under a section")
    public ResponseEntity<SubsectionResponse> createSubsection(
            @Parameter(description = "Section ID") @PathVariable String sectionId,
            @Valid @RequestBody SubsectionRequest request) {
        return ResponseEntity.ok(sectionService.createSubsection(sectionId, request));
    }

    @PutMapping("/subsections/{subsectionId}")
    @Operation(summary = "Update a subsection")
    public ResponseEntity<SubsectionResponse> updateSubsection(
            @Parameter(description = "Subsection ID") @PathVariable String subsectionId,
            @Valid @RequestBody SubsectionRequest request) {
        return ResponseEntity.ok(sectionService.updateSubsection(subsectionId, request));
    }

    @DeleteMapping("/subsections/{subsectionId}")
    @Operation(summary = "Delete a subsection")
    public ResponseEntity<Map<String, String>> deleteSubsection(
            @Parameter(description = "Subsection ID") @PathVariable String subsectionId) {
        sectionService.deleteSubsection(subsectionId);
        return ResponseEntity.ok(Map.of("message", "Subsection deleted successfully"));
    }
}
