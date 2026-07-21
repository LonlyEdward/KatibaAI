package com.katibaai.backend.chat.controller;

import com.katibaai.backend.ai.chat.ChatService;
import com.katibaai.backend.ai.chat.ChatResponse;
import com.katibaai.backend.chat.entity.ChatSession;
import com.katibaai.backend.chat.entity.Message;
import com.katibaai.backend.chat.repository.ChatSessionRepository;
import com.katibaai.backend.chat.repository.MessageRepository;
import com.katibaai.backend.user.entity.User;
import com.katibaai.backend.user.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Controller
@Validated
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatSessionRepository sessionRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @MutationMapping
    public ChatResponse askQuestion(
            @Argument @NotBlank(message = "Question cannot be empty")
            @Size(max = 2000, message = "Question must be under 2000 characters") String question,
            @Argument String sessionId,
            Authentication authentication) {

        User user = resolveUser(authentication);
        UUID parsedSessionId = (sessionId != null) ? UUID.fromString(sessionId) : null;
        return chatService.askQuestion(user, parsedSessionId, question);
    }

    @QueryMapping
    public List<ChatSession> mySessions(Authentication authentication) {
        User user = resolveUser(authentication);
        return sessionRepository.findByUserOrderByUpdatedAtDesc(user);
    }

    @Transactional
    @QueryMapping
    public ChatSessionDetailResult session(@Argument String id, Authentication authentication) {
        User user = resolveUser(authentication);
        UUID sessionUuid = UUID.fromString(id);

        ChatSession session = sessionRepository.findById(sessionUuid)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Session not found");
        }

        List<Message> messages = messageRepository.findBySessionOrderByCreatedAtAsc(session);
        return new ChatSessionDetailResult(session, messages);
    }

    private User resolveUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + email));
    }

    public record ChatSessionDetailResult(ChatSession session, List<Message> messages) {
        public UUID getId() { return session.getId(); }
        public String getTitle() { return session.getTitle(); }
        public List<Message> getMessages() { return messages; }
    }
}