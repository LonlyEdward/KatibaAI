package com.katibaai.backend.legal.ingestion;

import com.katibaai.backend.legal.ingestion.ParsedChunk;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ChunkingService {

    private static final int MAX_WORDS_PER_CHUNK = 400;
    private static final Pattern SUBARTICLE_SPLIT = Pattern.compile("(?=\\d+\\. )");

    public List<ParsedChunk> chunk(List<ParsedChunk> articles) {
        List<ParsedChunk> result = new ArrayList<>();
        for (ParsedChunk article : articles) {
            if (countWords(article.getContent()) <= MAX_WORDS_PER_CHUNK) {
                result.add(article.toBuilder().chunkIndex(0).build());
            } else {
                result.addAll(splitArticle(article));
            }
        }
        return result;
    }

    private List<ParsedChunk> splitArticle(ParsedChunk article) {
        String[] segments = SUBARTICLE_SPLIT.split(article.getContent());

        List<ParsedChunk> pieces = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int wordsSoFar = 0;

        for (String segment : segments) {
            int segWords = countWords(segment);
            if (wordsSoFar + segWords > MAX_WORDS_PER_CHUNK && !current.isEmpty()) {
                pieces.add(article.toBuilder().content(current.toString().trim()).build());
                current = new StringBuilder();
                wordsSoFar = 0;
            }
            current.append(segment);
            wordsSoFar += segWords;
        }
        if (!current.isEmpty()) {
            pieces.add(article.toBuilder().content(current.toString().trim()).build());
        }

        // Fallback: if a single subarticle alone still exceeds the limit, hard-split by word count.
        List<ParsedChunk> finalPieces = new ArrayList<>();
        for (ParsedChunk piece : pieces) {
            if (countWords(piece.getContent()) <= (int) (MAX_WORDS_PER_CHUNK * 1.5)) {
                finalPieces.add(piece);
            } else {
                finalPieces.addAll(hardSplit(piece));
            }
        }

        List<ParsedChunk> indexed = new ArrayList<>();
        int idx = 0;
        for (ParsedChunk piece : finalPieces) {
            indexed.add(piece.toBuilder().chunkIndex(idx++).build());
        }
        return indexed;
    }

    private List<ParsedChunk> hardSplit(ParsedChunk piece) {
        List<ParsedChunk> result = new ArrayList<>();
        String[] words = piece.getContent().split("\\s+");
        StringBuilder current = new StringBuilder();
        int count = 0;

        for (String word : words) {
            current.append(word).append(" ");
            count++;
            if (count >= MAX_WORDS_PER_CHUNK) {
                result.add(piece.toBuilder().content(current.toString().trim()).build());
                current = new StringBuilder();
                count = 0;
            }
        }
        if (!current.isEmpty()) {
            result.add(piece.toBuilder().content(current.toString().trim()).build());
        }
        return result;
    }

    private int countWords(String text) {
        if (text == null || text.isBlank()) return 0;
        return text.trim().split("\\s+").length;
    }
}