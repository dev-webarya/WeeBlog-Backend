package com.blogapp.section.repository;

import com.blogapp.section.entity.Section;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SectionRepository extends MongoRepository<Section, String> {

    Optional<Section> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);
}
