package com.katibaai.backend.legal.repository;

import com.katibaai.backend.legal.enums.DocumentType;
import com.katibaai.backend.legal.entity.LegalDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LegalDocumentRepository extends JpaRepository<LegalDocument, UUID> {

    Optional<LegalDocument> findByTitle(String title);

    List<LegalDocument> findByDocumentType(DocumentType documentType);

    List<LegalDocument> findByActiveTrue();

    boolean existsByTitle(String title);
}