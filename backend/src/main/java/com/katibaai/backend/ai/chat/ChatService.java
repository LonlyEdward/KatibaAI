package com.katibaai.backend.ai.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katibaai.backend.ai.prompt.PromptBuilder;
import com.katibaai.backend.ai.retrieval.RetrievalService;
import com.katibaai.backend.ai.retrieval.RetrievedChunk;
import com.katibaai.backend.chat.entity.ChatSession;
import com.katibaai.backend.chat.entity.Message;
import com.katibaai.backend.chat.entity.MessageRole;
import com.katibaai.backend.chat.repository.ChatSessionRepository;
import com.katibaai.backend.chat.repository.MessageRepository;
import com.katibaai.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RetrievalService retrievalService;
    private final PromptBuilder promptBuilder;
    private final ChatClient chatClient;
    private final ChatSessionRepository sessionRepository;
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ChatResponse askQuestion(User user, UUID sessionId, String question) {
        ChatSession session = (sessionId != null)
                ? sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"))
                : createSession(user, question);

        List<Message> recentHistory = messageRepository.findTop6BySessionOrderByCreatedAtDesc(session);

        List<RetrievedChunk> chunks = retrievalService.retrieveRelevantChunks(question);
        String prompt = promptBuilder.buildPrompt(question, chunks, recentHistory);

        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        saveMessage(session, MessageRole.USER, question, null);

        List<String> articleNumbers = chunks.stream()
                .map(RetrievedChunk::getArticleNumber)
                .collect(Collectors.toList());
        String sourcesJson = toJson(articleNumbers);

        saveMessage(session, MessageRole.ASSISTANT, answer, sourcesJson);

        session.setUpdatedAt(OffsetDateTime.now());
        sessionRepository.save(session);

        List<Source> sources = chunks.stream()
                .map(c -> Source.builder()
                        .articleNumber(c.getArticleNumber())
                        .chapterTitle(c.getChapterTitle())
                        .distance(c.getDistance())
                        .build())
                .collect(Collectors.toList());

        return ChatResponse.builder()
                .sessionId(session.getId())
                .answer(answer)
                .sources(sources)
                .build();
    }

    private ChatSession createSession(User user, String firstQuestion) {
        String title = firstQuestion.length() > 60
                ? firstQuestion.substring(0, 60) + "..."
                : firstQuestion;

        ChatSession session = ChatSession.builder()
                .user(user)
                .title(title)
                .build();

        return sessionRepository.save(session);
    }

    private void saveMessage(ChatSession session, MessageRole role, String content, String sources) {
        Message message = Message.builder()
                .session(session)
                .role(role)
                .content(content)
                .sources(sources)
                .build();
        messageRepository.save(message);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return null;
        }
    }
}