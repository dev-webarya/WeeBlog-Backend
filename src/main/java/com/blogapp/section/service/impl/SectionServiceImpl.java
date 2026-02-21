package com.blogapp.section.service.impl;

import com.blogapp.common.exception.BadRequestException;
import com.blogapp.common.exception.ResourceNotFoundException;
import com.blogapp.common.util.SlugUtil;
import com.blogapp.section.dto.request.SectionRequest;
import com.blogapp.section.dto.request.SubsectionRequest;
import com.blogapp.section.dto.response.SectionResponse;
import com.blogapp.section.dto.response.SubsectionResponse;
import com.blogapp.section.entity.Section;
import com.blogapp.section.entity.Subsection;
import com.blogapp.section.repository.SectionRepository;
import com.blogapp.section.repository.SubsectionRepository;
import com.blogapp.section.service.SectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final SubsectionRepository subsectionRepository;

    // ===================== PUBLIC =====================

    @Override
    public List<SectionResponse> getAllSections() {
        List<Section> sections = sectionRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder"));
        return sections.stream().map(this::toSectionResponseWithSubsections).collect(Collectors.toList());
    }

    @Override
    public SectionResponse getSectionBySlug(String slug) {
        Section section = sectionRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found: " + slug));
        return toSectionResponseWithSubsections(section);
    }

    @Override
    public List<SubsectionResponse> getSubsectionsBySectionSlug(String sectionSlug) {
        Section section = sectionRepository.findBySlug(sectionSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found: " + sectionSlug));
        return subsectionRepository.findBySectionIdOrderBySortOrderAsc(section.getId())
                .stream().map(this::toSubsectionResponse).collect(Collectors.toList());
    }

    // ===================== ADMIN =====================

    @Override
    public SectionResponse createSection(SectionRequest request) {
        String slug = SlugUtil.generateSlug(request.getName());
        if (sectionRepository.existsBySlug(slug)) {
            throw new BadRequestException("Section with name '" + request.getName() + "' already exists");
        }

        Section section = Section.builder()
                .name(request.getName().trim())
                .slug(slug)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();
        section = sectionRepository.save(section);
        log.info("Created section: {} ({})", section.getName(), section.getId());
        return toSectionResponse(section);
    }

    @Override
    public SectionResponse updateSection(String id, SectionRequest request) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found: " + id));

        section.setName(request.getName().trim());
        section.setSlug(SlugUtil.generateSlug(request.getName()));
        if (request.getSortOrder() != null) {
            section.setSortOrder(request.getSortOrder());
        }
        section = sectionRepository.save(section);
        log.info("Updated section: {} ({})", section.getName(), section.getId());
        return toSectionResponseWithSubsections(section);
    }

    @Override
    public void deleteSection(String id) {
        if (!sectionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Section not found: " + id);
        }
        // Also delete all subsections under this section
        List<Subsection> subsections = subsectionRepository.findBySectionIdOrderBySortOrderAsc(id);
        subsectionRepository.deleteAll(subsections);
        sectionRepository.deleteById(id);
        log.info("Deleted section {} and {} subsections", id, subsections.size());
    }

    @Override
    public SubsectionResponse createSubsection(String sectionId, SubsectionRequest request) {
        if (!sectionRepository.existsById(sectionId)) {
            throw new ResourceNotFoundException("Section not found: " + sectionId);
        }

        String slug = SlugUtil.generateSlug(request.getName());
        if (subsectionRepository.existsBySectionIdAndSlug(sectionId, slug)) {
            throw new BadRequestException("Subsection '" + request.getName() + "' already exists in this section");
        }

        Subsection subsection = Subsection.builder()
                .sectionId(sectionId)
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();
        subsection = subsectionRepository.save(subsection);
        log.info("Created subsection: {} ({}) under section {}", subsection.getName(), subsection.getId(), sectionId);
        return toSubsectionResponse(subsection);
    }

    @Override
    public SubsectionResponse updateSubsection(String subsectionId, SubsectionRequest request) {
        Subsection subsection = subsectionRepository.findById(subsectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subsection not found: " + subsectionId));

        subsection.setName(request.getName().trim());
        subsection.setSlug(SlugUtil.generateSlug(request.getName()));
        subsection.setDescription(request.getDescription());
        if (request.getSortOrder() != null) {
            subsection.setSortOrder(request.getSortOrder());
        }
        subsection = subsectionRepository.save(subsection);
        log.info("Updated subsection: {} ({})", subsection.getName(), subsection.getId());
        return toSubsectionResponse(subsection);
    }

    @Override
    public void deleteSubsection(String subsectionId) {
        if (!subsectionRepository.existsById(subsectionId)) {
            throw new ResourceNotFoundException("Subsection not found: " + subsectionId);
        }
        subsectionRepository.deleteById(subsectionId);
        log.info("Deleted subsection {}", subsectionId);
    }

    // ===================== INTERNAL HELPERS =====================

    @Override
    public String resolveSectionId(String sectionSlug) {
        return sectionRepository.findBySlug(sectionSlug)
                .map(Section::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found: " + sectionSlug));
    }

    @Override
    public String resolveSubsectionId(String sectionSlug, String subsectionSlug) {
        String sectionId = resolveSectionId(sectionSlug);
        return subsectionRepository.findBySectionIdAndSlug(sectionId, subsectionSlug)
                .map(Subsection::getId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subsection not found: " + subsectionSlug + " in section " + sectionSlug));
    }

    // ===================== MAPPERS =====================

    private SectionResponse toSectionResponse(Section section) {
        return SectionResponse.builder()
                .id(section.getId())
                .name(section.getName())
                .slug(section.getSlug())
                .sortOrder(section.getSortOrder())
                .build();
    }

    private SectionResponse toSectionResponseWithSubsections(Section section) {
        List<SubsectionResponse> subs = subsectionRepository
                .findBySectionIdOrderBySortOrderAsc(section.getId())
                .stream().map(this::toSubsectionResponse).collect(Collectors.toList());

        return SectionResponse.builder()
                .id(section.getId())
                .name(section.getName())
                .slug(section.getSlug())
                .sortOrder(section.getSortOrder())
                .subsections(subs)
                .build();
    }

    private SubsectionResponse toSubsectionResponse(Subsection subsection) {
        return SubsectionResponse.builder()
                .id(subsection.getId())
                .sectionId(subsection.getSectionId())
                .name(subsection.getName())
                .slug(subsection.getSlug())
                .description(subsection.getDescription())
                .sortOrder(subsection.getSortOrder())
                .build();
    }
}
