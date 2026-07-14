package com.katibaai.backend.ai.chat;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class ChatResponse {
    UUID sessionId;
    String answer;
    List<Source> sources;
}