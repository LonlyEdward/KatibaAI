package com.katibaai.backend.ai.prompt;

import com.katibaai.backend.ai.retrieval.RetrievedChunk;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {

    private static final String SYSTEM_INSTRUCTIONS = """
            You are KatibaAI, a legal assistant that answers questions about the \
            Constitution of the United Republic of Tanzania (1977, as amended through 2005).

            Rules:
            - Answer ONLY using the constitutional excerpts provided below.
            - Always cite the specific Article number(s) your answer is based on.
            - If the provided excerpts do not contain enough information to answer \
            the question, say so clearly rather than guessing or using outside knowledge.
            - Be clear and concise. This may be read by non-lawyers, so avoid unnecessary \
            legal jargon where a plain explanation works just as well.
            """;

    public String buildPrompt(String question, List<RetrievedChunk> chunks) {
        StringBuilder context = new StringBuilder();

        for (RetrievedChunk chunk : chunks) {
            context.append("Article ").append(chunk.getArticleNumber());
            if (chunk.getChapterTitle() != null) {
                context.append(" (Chapter ").append(chunk.getChapterNumber())
                        .append(": ").append(chunk.getChapterTitle()).append(")");
            }
            context.append(":\n").append(chunk.getContent()).append("\n\n");
        }

        return SYSTEM_INSTRUCTIONS
                + "\n\nCONSTITUTIONAL EXCERPTS:\n\n" + context
                + "\nQUESTION: " + question
                + "\n\nANSWER:";
    }
}