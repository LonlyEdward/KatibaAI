package com.katibaai.backend.ai.retrieval;

import com.katibaai.backend.ai.embedding.EmbeddingService;
import com.katibaai.backend.legal.entity.LegalDocument;
import com.katibaai.backend.legal.entity.LegalDocumentChunk;
import com.katibaai.backend.legal.repository.LegalDocumentChunkRepository;
import com.katibaai.backend.legal.repository.LegalDocumentRepository;
import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetrievalService {

    private static final UUID CONSTITUTION_DOCUMENT_ID =
            UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private static final int DEFAULT_TOP_K = 5;

    private static final Pattern ARTICLE_REFERENCE_PATTERN =
            Pattern.compile("article\\s+(\\d+[a-zA-Z]?)", Pattern.CASE_INSENSITIVE);

    private final EmbeddingService embeddingService;
    private final JdbcTemplate jdbcTemplate;
    private final LegalDocumentRepository documentRepository;
    private final LegalDocumentChunkRepository chunkRepository;

    public List<RetrievedChunk> retrieveRelevantChunks(String question) {
        return retrieveRelevantChunks(question, DEFAULT_TOP_K);
    }

    public List<RetrievedChunk> retrieveRelevantChunks(String question, int topK) {
        List<RetrievedChunk> directMatches = findDirectArticleMatches(question);

        List<RetrievedChunk> semanticMatches = runSemanticSearch(question, topK);

        Set<String> alreadyIncluded = directMatches.stream()
                .map(c -> c.getArticleNumber() + "-" + c.getContent().hashCode())
                .collect(Collectors.toSet());

        List<RetrievedChunk> combined = new ArrayList<>(directMatches);
        for (RetrievedChunk semantic : semanticMatches) {
            String key = semantic.getArticleNumber() + "-" + semantic.getContent().hashCode();
            if (!alreadyIncluded.contains(key)) {
                combined.add(semantic);
            }
        }

        return combined;
    }

    private List<RetrievedChunk> findDirectArticleMatches(String question) {
        Matcher matcher = ARTICLE_REFERENCE_PATTERN.matcher(question);
        if (!matcher.find()) {
            return List.of();
        }

        String articleNumber = matcher.group(1).toUpperCase();

        LegalDocument document = documentRepository.findById(CONSTITUTION_DOCUMENT_ID)
                .orElse(null);
        if (document == null) {
            return List.of();
        }

        List<LegalDocumentChunk> chunks =
                chunkRepository.findByDocumentAndArticleNumber(document, articleNumber);

        return chunks.stream()
                .map(chunk -> RetrievedChunk.builder()
                        .chapterNumber(chunk.getChapterNumber())
                        .chapterTitle(chunk.getChapterTitle())
                        .partNumber(chunk.getPartNumber())
                        .partTitle(chunk.getPartTitle())
                        .sectionNumber(chunk.getSectionNumber())
                        .sectionTitle(chunk.getSectionTitle())
                        .articleNumber(chunk.getArticleNumber())
                        .content(chunk.getContent())
                        .distance(0.0)
                        .build())
                .collect(Collectors.toList());
    }

    private List<RetrievedChunk> runSemanticSearch(String question, int topK) {
        float[] queryEmbedding = embeddingService.embed(question);
        PGvector queryVector = new PGvector(queryEmbedding);

        String sql = """
                SELECT
                    chapter_number, chapter_title,
                    part_number, part_title,
                    section_number, section_title,
                    article_number, content,
                    embedding <=> ? AS distance
                FROM legal_document_chunks
                WHERE document_id = ?
                ORDER BY embedding <=> ?
                LIMIT ?
                """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> RetrievedChunk.builder()
                        .chapterNumber((Integer) rs.getObject("chapter_number"))
                        .chapterTitle(rs.getString("chapter_title"))
                        .partNumber((Integer) rs.getObject("part_number"))
                        .partTitle(rs.getString("part_title"))
                        .sectionNumber((Integer) rs.getObject("section_number"))
                        .sectionTitle(rs.getString("section_title"))
                        .articleNumber(rs.getString("article_number"))
                        .content(rs.getString("content"))
                        .distance(rs.getDouble("distance"))
                        .build(),
                queryVector, CONSTITUTION_DOCUMENT_ID, queryVector, topK);
    }
}