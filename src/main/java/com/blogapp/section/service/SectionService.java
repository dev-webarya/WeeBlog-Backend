package com.blogapp.section.service;

import com.blogapp.section.dto.request.SectionRequest;
import com.blogapp.section.dto.request.SubsectionRequest;
import com.blogapp.section.dto.response.SectionResponse;
import com.blogapp.section.dto.response.SubsectionResponse;

import java.util.List;

public interface SectionService {

    // ---- Public ----
    List<SectionResponse> getAllSections();

    SectionResponse getSectionBySlug(String slug);

    List<SubsectionResponse> getSubsectionsBySectionSlug(String sectionSlug);

    // ---- Admin ----
    SectionResponse createSection(SectionRequest request);

    SectionResponse updateSection(String id, SectionRequest request);

    void deleteSection(String id);

    SubsectionResponse createSubsection(String sectionId, SubsectionRequest request);

    SubsectionResponse updateSubsection(String subsectionId, SubsectionRequest request);

    void deleteSubsection(String subsectionId);

    // ---- Internal helpers ----
    String resolveSectionId(String sectionSlug);

    String resolveSubsectionId(String sectionSlug, String subsectionSlug);
}
