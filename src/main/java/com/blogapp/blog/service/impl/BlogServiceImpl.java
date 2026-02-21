package com.blogapp.blog.service.impl;

import com.blogapp.blog.dto.request.CreateBlogRequest;
import com.blogapp.blog.dto.response.ArchiveResponse;
import com.blogapp.blog.dto.response.BlogDetailResponse;
import com.blogapp.blog.dto.response.BlogSummaryResponse;
import com.blogapp.blog.entity.BlogPost;
import com.blogapp.blog.enums.BlogStatus;
import com.blogapp.blog.mapper.BlogMapper;
import com.blogapp.blog.repository.BlogPostRepository;
import com.blogapp.blog.service.BlogService;
import com.blogapp.common.dto.PageResponse;
import com.blogapp.common.util.ContentSplitter;
import com.blogapp.common.exception.BadRequestException;
import com.blogapp.common.exception.ResourceNotFoundException;
import com.blogapp.common.util.HtmlSanitizer;
import com.blogapp.common.util.SlugUtil;
import com.blogapp.section.entity.Section;
import com.blogapp.section.entity.Subsection;
import com.blogapp.section.repository.SectionRepository;
import com.blogapp.section.repository.SubsectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogPostRepository blogPostRepository;
    private final BlogMapper blogMapper;
    private final MongoTemplate mongoTemplate;
    private final SectionRepository sectionRepository;
    private final SubsectionRepository subsectionRepository;

    // ===================== Section resolution helpers =====================

    /**
     * Bulk-load sections/subsections referenced by a list of blog posts
     * and return resolved summary responses.
     */
    private List<BlogSummaryResponse> mapToSummaryResponses(List<BlogPost> blogs) {
        Map<String, Section> sectionCache = loadSections(blogs);
        Map<String, Subsection> subsectionCache = loadSubsections(blogs);

        return blogs.stream()
                .map(blog -> blogMapper.toSummaryResponse(
                        blog,
                        sectionCache.get(blog.getSectionId()),
                        subsectionCache.get(blog.getSubsectionId())))
                .collect(Collectors.toList());
    }

    private BlogDetailResponse mapToDetailResponse(BlogPost blog) {
        Section section = blog.getSectionId() != null ? sectionRepository.findById(blog.getSectionId()).orElse(null)
                : null;
        Subsection subsection = blog.getSubsectionId() != null
                ? subsectionRepository.findById(blog.getSubsectionId()).orElse(null)
                : null;
        return blogMapper.toDetailResponse(blog, section, subsection);
    }

    private Map<String, Section> loadSections(List<BlogPost> blogs) {
        Set<String> ids = blogs.stream()
                .map(BlogPost::getSectionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty())
            return Collections.emptyMap();
        return sectionRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Section::getId, Function.identity()));
    }

    private Map<String, Subsection> loadSubsections(List<BlogPost> blogs) {
        Set<String> ids = blogs.stream()
                .map(BlogPost::getSubsectionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty())
            return Collections.emptyMap();
        return subsectionRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Subsection::getId, Function.identity()));
    }

    // ===================== Public endpoints =====================

    @Override
    public PageResponse<BlogSummaryResponse> getPublishedBlogs(String search, Integer year, Integer month,
            String sectionId, String subsectionId,
            String sort, int page, int size) {
        Sort sortOrder = resolveSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        // Build dynamic criteria
        Criteria criteria = Criteria.where("status").is(BlogStatus.PUBLISHED.name());

        if (sectionId != null && !sectionId.isBlank()) {
            criteria = criteria.and("sectionId").is(sectionId);
        }
        if (subsectionId != null && !subsectionId.isBlank()) {
            criteria = criteria.and("subsectionId").is(subsectionId);
        }
        if (year != null) {
            criteria = criteria.and("year").is(year);
        }
        if (month != null) {
            criteria = criteria.and("month").is(month);
        }
        if (search != null && !search.isBlank()) {
            criteria = criteria.orOperator(
                    Criteria.where("title").regex(search, "i"),
                    Criteria.where("excerpt").regex(search, "i"),
                    Criteria.where("tags").regex(search, "i"));
        }

        Query query = new Query(criteria).with(pageable);
        List<BlogPost> blogs = mongoTemplate.find(query, BlogPost.class);
        long total = mongoTemplate.count(new Query(criteria), BlogPost.class);

        List<BlogSummaryResponse> content = mapToSummaryResponses(blogs);

        return PageResponse.<BlogSummaryResponse>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .first(page == 0)
                .last(page >= (int) Math.ceil((double) total / size) - 1)
                .build();
    }

    @Override
    public BlogDetailResponse getBlogBySlug(String slug) {
        BlogPost blog = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Blog", "slug", slug));

        if (blog.getStatus() != BlogStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Blog", "slug", slug);
        }

        return mapToDetailResponse(blog);
    }

    @Override
    public BlogDetailResponse getBlogById(String id) {
        BlogPost blog = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog", "id", id));
        return mapToDetailResponse(blog);
    }

    @Override
    public List<ArchiveResponse> getArchive() {
        return getArchive(null);
    }

    /**
     * Get archive scoped to an optional sectionId.
     */
    public List<ArchiveResponse> getArchive(String sectionId) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("status").is(BlogStatus.PUBLISHED.name()));
        if (sectionId != null && !sectionId.isBlank()) {
            criteriaList.add(Criteria.where("sectionId").is(sectionId));
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0]))),
                Aggregation.group("year", "month").count().as("count"),
                Aggregation.sort(Sort.Direction.DESC, "_id.year", "_id.month"));

        AggregationResults<org.bson.Document> results = mongoTemplate.aggregate(
                aggregation, "blog_posts", org.bson.Document.class);

        Map<Integer, List<ArchiveResponse.MonthCount>> yearMap = new TreeMap<>(Comparator.reverseOrder());

        for (org.bson.Document doc : results.getMappedResults()) {
            org.bson.Document idDoc = doc.get("_id", org.bson.Document.class);
            if (idDoc == null)
                continue;

            Integer year = idDoc.getInteger("year");
            Integer month = idDoc.getInteger("month");
            long count = doc.getInteger("count", 0);

            if (year != null && month != null) {
                yearMap.computeIfAbsent(year, k -> new ArrayList<>())
                        .add(ArchiveResponse.MonthCount.builder()
                                .month(month)
                                .count(count)
                                .build());
            }
        }

        return yearMap.entrySet().stream()
                .map(entry -> ArchiveResponse.builder()
                        .year(entry.getKey())
                        .months(entry.getValue().stream()
                                .sorted(Comparator.comparingInt(ArchiveResponse.MonthCount::getMonth).reversed())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    // ===================== Blog write operations =====================

    @Override
    public BlogPost createBlog(CreateBlogRequest request, String authorName, String authorEmail, String authorMobile) {
        BlogPost blog = blogMapper.toEntity(request);
        blog.setAuthorName(authorName);
        blog.setAuthorEmail(authorEmail);
        blog.setAuthorMobile(authorMobile);
        blog.setStatus(BlogStatus.PENDING);
        blog.setSubmittedAt(LocalDateTime.now());

        // Ensure unique slug
        String baseSlug = SlugUtil.generateSlug(request.getTitle());
        String slug = baseSlug;
        int counter = 1;
        while (blogPostRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        blog.setSlug(slug);

        log.info("Creating blog post with slug: {} by author: {}", slug, authorEmail);
        return blogPostRepository.save(blog);
    }

    @Override
    public BlogPost approveBlog(String id, String adminId, Integer internalRating, String sectionId,
            String subsectionId) {
        BlogPost blog = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog", "id", id));

        if (blog.getStatus() != BlogStatus.PENDING) {
            throw new BadRequestException("Only PENDING blogs can be approved. Current status: " + blog.getStatus());
        }

        blog.setStatus(BlogStatus.PUBLISHED);
        blog.setPublishedAt(LocalDateTime.now());
        blog.setApprovedByAdminId(adminId);
        blog.setYear(blog.getPublishedAt().getYear());
        blog.setMonth(blog.getPublishedAt().getMonthValue());
        blog.setRejectionReason(null);

        // Set internal rating if provided
        if (internalRating != null) {
            blog.setInternalRating(internalRating);
        }

        // Apply new taxonomy mapping if given
        if (sectionId != null) {
            blog.setSectionId(sectionId);
        }
        if (subsectionId != null) {
            blog.setSubsectionId(subsectionId);
        }

        // Auto-split content for premium blogs (rating > 6)
        if (blog.getInternalRating() != null && blog.getInternalRating() > 6
                && blog.getContentHtml() != null) {
            String[] parts = ContentSplitter.split(blog.getContentHtml());
            blog.setContentPart1Html(parts[0]);
            blog.setContentPart2Html(parts[1]);
        }

        log.info("Blog approved: {} by admin: {}, rating: {}", id, adminId, internalRating);
        return blogPostRepository.save(blog);
    }

    @Override
    public BlogPost rejectBlog(String id, String reason) {
        BlogPost blog = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog", "id", id));

        if (blog.getStatus() != BlogStatus.PENDING) {
            throw new BadRequestException("Only PENDING blogs can be rejected. Current status: " + blog.getStatus());
        }

        blog.setStatus(BlogStatus.REJECTED);
        blog.setRejectionReason(reason);

        log.info("Blog rejected: {} — reason: {}", id, reason);
        return blogPostRepository.save(blog);
    }

    @Override
    public BlogPost updateBlog(String id, CreateBlogRequest request) {
        BlogPost blog = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog", "id", id));

        blog.setTitle(request.getTitle());
        blog.setExcerpt(request.getExcerpt());
        blog.setContentHtml(HtmlSanitizer.sanitize(request.getContentHtml()));
        blog.setContentJson(request.getContentJson());
        blog.setFeaturedImageUrl(request.getFeaturedImageUrl());
        blog.setTags(request.getTags());
        blog.setSectionId(request.getSectionId());
        blog.setSubsectionId(request.getSubsectionId());

        log.info("Blog updated: {}", id);
        return blogPostRepository.save(blog);
    }

    // ===================== Admin endpoints =====================

    @Override
    public PageResponse<BlogDetailResponse> getAdminBlogs(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedAt"));
        Page<BlogPost> blogPage;

        if (status != null && !status.isBlank()) {
            try {
                BlogStatus blogStatus = BlogStatus.valueOf(status.toUpperCase());
                blogPage = blogPostRepository.findByStatus(blogStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + status
                        + ". Valid values: DRAFT, PENDING, PUBLISHED, REJECTED");
            }
        } else {
            blogPage = blogPostRepository.findAll(pageable);
        }

        List<BlogDetailResponse> content = blogPage.getContent().stream()
                .map(this::mapToDetailResponse)
                .collect(Collectors.toList());

        return PageResponse.<BlogDetailResponse>builder()
                .content(content)
                .page(blogPage.getNumber())
                .size(blogPage.getSize())
                .totalElements(blogPage.getTotalElements())
                .totalPages(blogPage.getTotalPages())
                .first(blogPage.isFirst())
                .last(blogPage.isLast())
                .build();
    }

    @Override
    public void incrementViewCount(String id) {
        BlogPost blog = blogPostRepository.findById(id).orElse(null);
        if (blog != null) {
            blog.setViewsCount(blog.getViewsCount() + 1);
            blogPostRepository.save(blog);
        }
    }

    private Sort resolveSort(String sort) {
        if (sort == null || sort.isBlank() || sort.equalsIgnoreCase("recent")) {
            return Sort.by(Sort.Direction.DESC, "publishedAt");
        } else if (sort.equalsIgnoreCase("popular")) {
            return Sort.by(Sort.Direction.DESC, "likesCount");
        } else if (sort.equalsIgnoreCase("oldest")) {
            return Sort.by(Sort.Direction.ASC, "publishedAt");
        } else if (sort.equalsIgnoreCase("most_commented")) {
            return Sort.by(Sort.Direction.DESC, "commentsCount");
        }
        return Sort.by(Sort.Direction.DESC, "publishedAt");
    }
}
