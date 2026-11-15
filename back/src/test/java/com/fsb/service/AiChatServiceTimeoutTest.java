package com.fsb.service;

import com.fsb.auth.AiContextSignature;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.AiChatRequest;
import com.fsb.Service.impl.AiChatServiceImpl;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AiChatServiceTimeoutTest {
    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    @Test
    void regularChatTurnsAdapterReadTimeoutIntoStableGatewayTimeout() throws Exception {
        HttpServer server = slowServer("/chat");
        try {
            AiChatServiceImpl service = service(server, 80);
            AuthException error = assertThrows(AuthException.class,
                    () -> service.chat(request(), 7L, "alice"));

            assertEquals(504, error.getCode());
            assertTrue(error.getMessage().startsWith("UPSTREAM_TIMEOUT:"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void streamingChatEmitsSseErrorWhenAdapterReadTimesOut() throws Exception {
        HttpServer server = slowServer("/chat/stream");
        try {
            AiChatServiceImpl service = service(server, 80);
            StreamingResponseBody body = service.chatStream(request(), 7L, "alice");
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            body.writeTo(output);

            String event = output.toString(StandardCharsets.UTF_8);
            assertTrue(event.contains("\"type\":\"error\""));
            assertTrue(event.contains("超时"));
        } finally {
            server.stop(0);
        }
    }

    private AiChatServiceImpl service(HttpServer server, int readTimeoutMs) {
        return new AiChatServiceImpl("http://127.0.0.1:" + server.getAddress().getPort(),
                "test-service-token", 1000, readTimeoutMs, new AiContextSignature(SECRET));
    }

    private AiChatRequest request() {
        AiChatRequest request = new AiChatRequest();
        request.setMessage("hello");
        return request;
    }

    private HttpServer slowServer(String path) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(path, exchange -> {
            try {
                Thread.sleep(250);
                byte[] response = "{}".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } finally {
                exchange.close();
            }
        });
        server.start();
        return server;
    }
}
