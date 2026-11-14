package com.fsb.service.impl;

import com.fsb.pojo.DTO.AiChatRequest;
import com.fsb.auth.AiContextSignature;
import com.fsb.service.AiChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.time.Duration;
import java.net.SocketTimeoutException;

@Service
public class AiChatServiceImpl implements AiChatService {

    private final RestClient restClient;
    private final String adapterToken;
    private final AiContextSignature contextSignature;

    public AiChatServiceImpl(
            @Value("${ai.adapter-url:http://127.0.0.1:8091}") String adapterUrl,
            @Value("${ai.internal-token:}") String adapterToken,
            @Value("${ai.adapter-connect-timeout-ms:3000}") int connectTimeoutMs,
            @Value("${ai.adapter-read-timeout-ms:120000}") int readTimeoutMs,
            AiContextSignature contextSignature) {
        if (connectTimeoutMs < 1 || readTimeoutMs < 1) {
            throw new IllegalArgumentException("AI adapter timeouts must be positive");
        }
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));
        this.restClient = RestClient.builder().baseUrl(adapterUrl).requestFactory(requestFactory).build();
        this.adapterToken = adapterToken;
        this.contextSignature = contextSignature;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> chat(AiChatRequest request, Long userId, String username) {
        String conversationId = request.getConversationId();
        if (conversationId == null || conversationId.isBlank()) {
            conversationId = "web-" + userId + "-" + UUID.randomUUID();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("message", request.getMessage().trim());
        body.put("conversationId", conversationId);

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = UUID.randomUUID().toString();
        Map<String, Object> response;
        try {
            response = restClient.post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AI-Service-Token", adapterToken)
                .header("X-AI-User-Id", String.valueOf(userId))
                .header("X-AI-Username", username)
                .header("X-AI-Context-Timestamp", timestamp)
                .header("X-AI-Context-Nonce", nonce)
                .header("X-AI-Context-Signature", contextSignature.signWebUser(String.valueOf(userId), timestamp, nonce))
                .header("X-AI-Conversation-Id", conversationId)
                .header("X-AI-Message-Id", UUID.randomUUID().toString())
                .body(body)
                .retrieve()
                    .body(Map.class);
        } catch (RestClientException ex) {
            throw adapterException(ex);
        }

        if (response == null) {
            throw new IllegalStateException("AI 客服没有返回结果");
        }
        return response;
    }

    @Override
    public StreamingResponseBody chatStream(AiChatRequest request, Long userId, String username) {
        String conversationId = request.getConversationId();
        if (conversationId == null || conversationId.isBlank()) {
            conversationId = "web-" + userId + "-" + UUID.randomUUID();
        }
        final String streamConversationId = conversationId;

        Map<String, Object> body = new HashMap<>();
        body.put("message", request.getMessage().trim());
        body.put("conversationId", streamConversationId);

        return outputStream -> {
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String nonce = UUID.randomUUID().toString();
            try {
                restClient.post()
                .uri("/chat/stream")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AI-Service-Token", adapterToken)
                .header("X-AI-User-Id", String.valueOf(userId))
                .header("X-AI-Username", username)
                .header("X-AI-Context-Timestamp", timestamp)
                .header("X-AI-Context-Nonce", nonce)
                .header("X-AI-Context-Signature", contextSignature.signWebUser(String.valueOf(userId), timestamp, nonce))
                .header("X-AI-Conversation-Id", streamConversationId)
                .header("X-AI-Message-Id", UUID.randomUUID().toString())
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
            } catch (RestClientException ex) {
                String message = isTimeout(ex)
                        ? "客服响应超时，请稍后重试。"
                        : "客服服务暂时不可用，请稍后重试。";
                byte[] event = ("data: {\"type\":\"error\",\"message\":\"" + message + "\"}\n\n")
                        .getBytes(StandardCharsets.UTF_8);
                outputStream.write(event);
                outputStream.flush();
            }
        };
    }

    private com.fsb.exception.AuthException adapterException(RestClientException exception) {
        if (isTimeout(exception)) {
            return new com.fsb.exception.AuthException(504,
                    "UPSTREAM_TIMEOUT: AI adapter did not respond before the timeout");
        }
        return new com.fsb.exception.AuthException(502,
                "UPSTREAM_ERROR: AI adapter request failed");
    }

    private boolean isTimeout(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof SocketTimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
