package com.fsb.service.impl;

import com.fsb.pojo.DTO.AiChatRequest;
import com.fsb.service.AiChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

@Service
public class AiChatServiceImpl implements AiChatService {

    private final RestClient restClient;
    private final String adapterToken;

    public AiChatServiceImpl(
            @Value("${ai.adapter-url:http://127.0.0.1:8091}") String adapterUrl,
            @Value("${ai.internal-token:bat-manager-ai-local-token}") String adapterToken) {
        this.restClient = RestClient.builder().baseUrl(adapterUrl).build();
        this.adapterToken = adapterToken;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> chat(AiChatRequest request, Long userId, String username) {
        String conversationId = request.getConversationId();
        if (conversationId == null || conversationId.isBlank()) {
            conversationId = "web-" + userId + "-" + System.currentTimeMillis();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("message", request.getMessage().trim());
        body.put("conversationId", conversationId);

        Map<String, Object> response = restClient.post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AI-Service-Token", adapterToken)
                .header("X-AI-User-Id", String.valueOf(userId))
                .header("X-AI-Username", username)
                .header("X-AI-Conversation-Id", conversationId)
                .body(body)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalStateException("AI 客服没有返回结果");
        }
        return response;
    }

    @Override
    public StreamingResponseBody chatStream(AiChatRequest request, Long userId, String username) {
        String conversationId = request.getConversationId();
        if (conversationId == null || conversationId.isBlank()) {
            conversationId = "web-" + userId + "-" + System.currentTimeMillis();
        }
        final String streamConversationId = conversationId;

        Map<String, Object> body = new HashMap<>();
        body.put("message", request.getMessage().trim());
        body.put("conversationId", streamConversationId);

        return outputStream -> restClient.post()
                .uri("/chat/stream")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AI-Service-Token", adapterToken)
                .header("X-AI-User-Id", String.valueOf(userId))
                .header("X-AI-Username", username)
                .header("X-AI-Conversation-Id", streamConversationId)
                .body(body)
                .exchange((requestHeaders, response) -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            outputStream.write((line + "\n").getBytes(StandardCharsets.UTF_8));
                            outputStream.flush();
                        }
                    }
                    return null;
                });
    }
}
