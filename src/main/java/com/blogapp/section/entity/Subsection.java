package com.blogapp.section.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "subsections")
@CompoundIndex(name = "section_slug_idx", def = "{'sectionId': 1, 'slug': 1}", unique = true)
public class Subsection {

    @Id
    private String id;

    @Indexed
    private String sectionId;

    @Indexed
    private String name;

    @Indexed
    private String slug;

    private String description;

    @Builder.Default
    private int sortOrder = 0;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
