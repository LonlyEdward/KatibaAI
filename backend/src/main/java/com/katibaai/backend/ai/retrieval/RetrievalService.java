package com.katibaai.backend.ai.retrieval;

import com.katibaai.backend.ai.embedding.EmbeddingService;
import com.katibaai.backend.ai.retrieval.RetrievedChunk;
import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RetrievalService {

    private static final UUID CONSTITUTION_DOCUMENT_ID =
            UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private static final int DEFAULT_TOP_K = 5;

    private final EmbeddingService embeddingService;
    private final JdbcTemplate jdbcTemplate;

    public List<RetrievedChunk> retrieveRelevantChunks(String question) {
        return retrieveRelevantChunks(question, DEFAULT_TOP_K);
    }

    public List<RetrievedChunk> retrieveRelevantChunks(String question, int topK) {
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