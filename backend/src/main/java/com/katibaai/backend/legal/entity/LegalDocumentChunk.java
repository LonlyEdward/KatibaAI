package com.katibaai.backend.legal.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "legal_document_chunks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalDocumentChunk {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private LegalDocument document;

    @Column(name = "chapter_number")
    private Integer chapterNumber;

    @Column(name = "chapter_title", length = 500)
    private String chapterTitle;

    @Column(name = "part_number")
    private Integer partNumber;

    @Column(name = "part_title", length = 500)
    private String partTitle;

    @Column(name = "section_number")
    private Integer sectionNumber;

    @Column(name = "section_title", length = 500)
    private String sectionTitle;

    @Column(name = "article_number", length = 10)
    private String articleNumber;

    @Column(name = "paragraph_number")
    private Integer paragraphNumber;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Transient
    private float[] embedding;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}