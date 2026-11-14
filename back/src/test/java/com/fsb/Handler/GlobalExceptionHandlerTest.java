package com.fsb.Handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsb.exception.AuthException;
import com.fsb.result.Result;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void returnsStableAiIdentityErrorCodeWithoutReplacingLegacyCode() {
        var response = handler.handleAuthException(new AuthException(401,
                "IDENTITY_NOT_BOUND: 当前平台身份尚未绑定商城账号"));

        assertEquals(401, response.getStatusCode().value());
        assertEquals(401, response.getBody().getCode());
        assertEquals("IDENTITY_NOT_BOUND", response.getBody().getErrorCode());
    }

    @Test
    void returnsNotFoundForAnOrderOutsideTheResolvedCustomer() {
        var response = handler.handleAuthException(new AuthException(404, "NOT_FOUND: 订单不存在"));

        assertEquals(404, response.getStatusCode().value());
        assertEquals("NOT_FOUND", response.getBody().getErrorCode());
    }

    @Test
    void successfulLegacyResponseOmitsOptionalErrorCode() throws Exception {
        JsonNode json = new ObjectMapper().readTree(new ObjectMapper().writeValueAsBytes(Result.success("ok")));
        assertEquals(1, json.get("code").asInt());
        assertEquals("ok", json.get("data").asText());
        assertFalse(json.has("errorCode"));
    }

    @Test
    void rateLimitedAuthResponseIncludesRetryAfterHeader() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        var response = handler.handleAuthException(new AuthException(429, "RATE_LIMITED: busy"));

        assertEquals(429, response.getStatusCode().value());
        assertEquals("1", response.getHeaders().getFirst("Retry-After"));
        assertEquals("RATE_LIMITED", response.getBody().getErrorCode());
    }

    @Test
    void forbiddenAuthResponseKeepsStableErrorCode() {
        var response = handler.handleAuthException(new AuthException(403,
                "FORBIDDEN: personal data is private-chat only"));

        assertEquals(403, response.getStatusCode().value());
        assertEquals("FORBIDDEN", response.getBody().getErrorCode());
    }

    @Test
    void adapterTimeoutReturnsGatewayTimeoutWithStableCode() {
        var response = handler.handleAuthException(new AuthException(504,
                "UPSTREAM_TIMEOUT: AI adapter did not respond before the timeout"));

        assertEquals(504, response.getStatusCode().value());
        assertEquals(504, response.getBody().getCode());
        assertEquals("UPSTREAM_TIMEOUT", response.getBody().getErrorCode());
    }

    @Test
    void mapsDependencyFailuresToRetryableUpstreamError() {
        var response = handler.handleDataAccessException(
                new DataAccessResourceFailureException("redis unavailable"));

        assertEquals(503, response.getStatusCode().value());
        assertEquals(503, response.getBody().getCode());
        assertEquals("UPSTREAM_ERROR", response.getBody().getErrorCode());
    }
}
