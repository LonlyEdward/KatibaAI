package com.katibaai.backend.legal.ingestion;

import com.katibaai.backend.legal.ingestion.ParsedChunk;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.IntArrayList;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ChunkingService {

    private static final int MIN_TOKENS_PER_CHUNK = 300;
    private static final int MAX_TOKENS_PER_CHUNK = 500;
    private static final double OVERLAP_RATIO = 0.10;

    private static final Pattern SUBARTICLE_SPLIT = Pattern.compile("(?=\\d+\\. )");

    private final Encoding encoding;

    public ChunkingService() {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        this.encoding = registry.getEncoding(EncodingType.CL100K_BASE);
    }

    public List<ParsedChunk> chunk(List<ParsedChunk> articles) {
        List<ParsedChunk> result = new ArrayList<>();
        for (ParsedChunk article : articles) {
            int tokenCount = countTokens(article.getContent());
            if (tokenCount <= MAX_TOKENS_PER_CHUNK) {
                result.add(article.toBuilder().chunkIndex(0).build());
            } else {
                result.addAll(splitArticle(article));
            }
        }
        return result;
    }

    private List<ParsedChunk> splitArticle(ParsedChunk article) {
        String[] segments = SUBARTICLE_SPLIT.split(article.getContent());
        if (segments.length <= 1) {
            segments = article.getContent().split("(?<=\\. )");
        }

        List<String> rawPieces = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int currentTokens = 0;

        for (String segment : segments) {
            int segTokens = countTokens(segment);

            if (currentTokens + segTokens > MAX_TOKENS_PER_CHUNK && currentTokens >= MIN_TOKENS_PER_CHUNK) {
                rawPieces.add(current.toString().trim());
                current = new StringBuilder();
                currentTokens = 0;
            }

            current.append(segment);
            currentTokens += segTokens;
        }
        if (!current.isEmpty()) {
            rawPieces.add(current.toString().trim());
        }

        List<String> overlapped = applyOverlap(rawPieces);

        List<ParsedChunk> indexed = new ArrayList<>();
        int idx = 0;
        for (String piece : overlapped) {
            indexed.add(article.toBuilder().content(piece).chunkIndex(idx++).build());
        }
        return indexed;
    }

    private List<String> applyOverlap(List<String> pieces) {
        if (pieces.size() <= 1) return pieces;

        List<String> result = new ArrayList<>();
        for (int i = 0; i < pieces.size(); i++) {
            if (i == 0) {
                result.add(pieces.get(i));
                continue;
            }
            String previous = pieces.get(i - 1);
            String overlapText = tailByTokens(previous, (int) Math.round(MAX_TOKENS_PER_CHUNK * OVERLAP_RATIO));
            result.add(overlapText + " " + pieces.get(i));
        }
        return result;
    }

    private String tailByTokens(String text, int tokenBudget) {
        IntArrayList tokens = encoding.encode(text);
        int size = tokens.size();
        if (size <= tokenBudget) return text;

        int[] all = tokens.toArray();
        IntArrayList tail = new IntArrayList(tokenBudget);
        for (int i = size - tokenBudget; i < size; i++) {
            tail.add(all[i]);
        }
        return encoding.decode(tail);
    }

    private int countTokens(String text) {
        if (text == null || text.isBlank()) return 0;
        return encoding.countTokens(text);
    }
}