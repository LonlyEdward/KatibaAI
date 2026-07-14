package com.katibaai.backend.ai.chat;

import com.katibaai.backend.ai.chat.ChatResponse;
import com.katibaai.backend.ai.prompt.PromptBuilder;
import com.katibaai.backend.ai.retrieval.RetrievalService;
import com.katibaai.backend.ai.retrieval.RetrievedChunk;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RetrievalService retrievalService;
    private final PromptBuilder promptBuilder;
    private final ChatClient chatClient;

    public ChatResponse askQuestion(String question) {
        List<RetrievedChunk> chunks = retrievalService.retrieveRelevantChunks(question);

        String prompt = promptBuilder.buildPrompt(question, chunks);

        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return ChatResponse.builder()
                .answer(answer)
                .sources(chunks)
                .build();
    }
}