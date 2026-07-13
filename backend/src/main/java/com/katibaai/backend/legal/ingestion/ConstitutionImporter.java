package com.katibaai.backend.legal.ingestion;

import com.katibaai.backend.ai.embedding.EmbeddingService;
import com.katibaai.backend.legal.entity.LegalDocument;
import com.katibaai.backend.legal.entity.LegalDocumentChunk;
import com.katibaai.backend.legal.repository.LegalDocumentChunkRepository;
import com.katibaai.backend.legal.repository.LegalDocumentRepository;
import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConstitutionImporter {

    private static final UUID CONSTITUTION_DOCUMENT_ID =
            UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private final DocumentReader documentReader;
    private final LegalStructureParser structureParser;
    private final ChunkingService chunkingService;
    private final EmbeddingService embeddingService;
    private final LegalDocumentRepository documentRepository;
    private final LegalDocumentChunkRepository chunkRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void importConstitution(File docxFile) {
        LegalDocument document = documentRepository.findById(CONSTITUTION_DOCUMENT_ID)
                .orElseThrow(() -> new IllegalStateException(
                        "Constitution document row not found — check V6 seed migration ran"));

        List<LegalDocumentChunk> existing = chunkRepository.findByDocument(document);
        if (!existing.isEmpty()) {
            throw new IllegalStateException(
                    "Document already has " + existing.size()
                            + " chunks. Delete them first if you want to re-import.");
        }

        System.out.println("Reading DOCX...");
        List<DocxParagraph> paragraphs = documentReader.readDocx(docxFile);

        System.out.println("Parsing structure...");
        List<ParsedChunk> articles = structureParser.parse(paragraphs);
        System.out.println("Parsed " + articles.size() + " articles.");

        System.out.println("Chunking...");
        List<ParsedChunk> chunks = chunkingService.chunk(articles);
        System.out.println("Produced " + chunks.size() + " chunks.");

        System.out.println("Generating embeddings and saving...");
        int saved = 0;
        for (ParsedChunk chunk : chunks) {
            float[] embedding = embeddingService.embed(chunk.getContent());

            LegalDocumentChunk entity = LegalDocumentChunk.builder()
                    .document(document)
                    .chapterNumber(chunk.getChapterNumber())
                    .chapterTitle(chunk.getChapterTitle())
                    .partNumber(chunk.getPartNumber())
                    .partTitle(chunk.getPartTitle())
                    .sectionNumber(chunk.getSectionNumber())
                    .sectionTitle(chunk.getSectionTitle())
                    .articleNumber(chunk.getArticleNumber())
                    .paragraphNumber(chunk.getParagraphNumber())
                    .chunkIndex(chunk.getChunkIndex())
                    .content(chunk.getContent())
                    .build();

            LegalDocumentChunk savedEntity = chunkRepository.saveAndFlush(entity);

            int rowsAffected = jdbcTemplate.update(
                    "UPDATE legal_document_chunks SET embedding = ? WHERE id = ?",
                    new PGvector(embedding),
                    savedEntity.getId()
            );

            if (rowsAffected != 1) {
                System.out.println("WARNING: embedding update affected " + rowsAffected + " rows for chunk id " + savedEntity.getId());
            }

            saved++;
            if (saved % 20 == 0) {
                System.out.println("Saved " + saved + "/" + chunks.size());
            }
        }

        System.out.println("Import complete. Total chunks saved: " + saved);
    }
}