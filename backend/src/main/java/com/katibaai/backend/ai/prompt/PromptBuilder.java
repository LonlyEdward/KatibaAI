package com.katibaai.backend.ai.prompt;

import com.katibaai.backend.ai.retrieval.RetrievedChunk;
import com.katibaai.backend.chat.entity.Message;
import com.katibaai.backend.chat.entity.MessageRole;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
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
            - If PREVIOUS CONVERSATION is provided below, use it to understand what \
            the current question is referring to (e.g. pronouns like "it" or "that right"), \
            but still answer strictly based on the constitutional excerpts, not on \
            anything stated in the previous conversation itself.
            """;

    public String buildPrompt(String question, List<RetrievedChunk> chunks, List<Message> recentHistory) {
        StringBuilder context = new StringBuilder();
        for (RetrievedChunk chunk : chunks) {
            context.append("Article ").append(chunk.getArticleNumber());
            if (chunk.getChapterTitle() != null) {
                context.append(" (Chapter ").append(chunk.getChapterNumber())
                        .append(": ").append(chunk.getChapterTitle()).append(")");
            }
            context.append(":\n").append(chunk.getContent()).append("\n\n");
        }

        String conversationBlock = buildConversationBlock(recentHistory);

        return SYSTEM_INSTRUCTIONS
                + "\n\nCONSTITUTIONAL EXCERPTS:\n\n" + context
                + conversationBlock
                + "\nQUESTION: " + question
                + "\n\nANSWER:";
    }

    private String buildConversationBlock(List<Message> recentHistory) {
        if (recentHistory == null || recentHistory.isEmpty()) {
            return "";
        }

        List<Message> chronological = new ArrayList<>(recentHistory);
        Collections.reverse(chronological);

        StringBuilder conversation = new StringBuilder();
        for (Message msg : chronological) {
            String speaker = msg.getRole() == MessageRole.USER ? "User" : "Assistant";
            conversation.append(speaker).append(": ").append(msg.getContent()).append("\n");
        }

        return "\nPREVIOUS CONVERSATION:\n" + conversation + "\n";
    }
}