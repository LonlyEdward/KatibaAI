package com.katibaai.backend.chat.repository;

import com.katibaai.backend.chat.entity.ChatSession;
import com.katibaai.backend.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findBySessionOrderByCreatedAtAsc(ChatSession session);

    List<Message> findTop6BySessionOrderByCreatedAtDesc(ChatSession session);
}