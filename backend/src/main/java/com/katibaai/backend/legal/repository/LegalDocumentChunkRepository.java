package com.katibaai.backend.legal.repository;

import com.katibaai.backend.legal.entity.LegalDocument;
import com.katibaai.backend.legal.entity.LegalDocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LegalDocumentChunkRepository extends JpaRepository<LegalDocumentChunk, UUID> {

    List<LegalDocumentChunk> findByDocument(LegalDocument document);

    List<LegalDocumentChunk> findByArticleNumber(String articleNumber);

    List<LegalDocumentChunk> findByDocumentAndArticleNumber(LegalDocument document, String articleNumber);

    List<LegalDocumentChunk> findByDocumentAndChapterNumber(LegalDocument document, Integer chapterNumber);

    List<LegalDocumentChunk> findByDocumentOrderByChunkIndex(LegalDocument document);
}