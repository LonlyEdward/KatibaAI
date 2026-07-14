package com.katibaai.backend.ai.chat;

import com.katibaai.backend.ai.retrieval.RetrievedChunk;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ChatResponse {
    String answer;
    List<RetrievedChunk> sources;
}