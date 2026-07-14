package com.katibaai.backend.ai.chat;

import com.katibaai.backend.ai.retrieval.RetrievedChunk;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class ChatResponse {
    UUID sessionId;
    String answer;
    List<RetrievedChunk> sources;
}