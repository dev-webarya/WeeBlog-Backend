package com.blogapp.section.repository;

import com.blogapp.section.entity.Subsection;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SubsectionRepository extends MongoRepository<Subsection, String> {

    List<Subsection> findBySectionIdOrderBySortOrderAsc(String sectionId);

    Optional<Subsection> findBySectionIdAndSlug(String sectionId, String slug);

    boolean existsBySectionIdAndSlug(String sectionId, String slug);

    boolean existsBySectionIdAndName(String sectionId, String name);
}
