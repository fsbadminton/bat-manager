package com.fsb.Handler;

import com.fsb.exception.AuthException;
import org.springframework.dao.DataAccessException;
import com.fsb.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Result<?>> handleAuthException(AuthException ex) {
        ResponseEntity.BodyBuilder response = ResponseEntity.status(ex.getCode());
        if (ex.getCode() == 429) {
            response.header(HttpHeaders.RETRY_AFTER, "1");
        }
        return response.body(Result.fail(ex.getCode(), ex.getMessage(),
                errorCodeFromMessage(ex.getMessage(), "UNAUTHENTICATED")));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("请求参数错误");
        return ResponseEntity.badRequest().body(Result.fail(400, message, "INVALID_ARGUMENT"));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Result<?>> handleDataAccessException(DataAccessException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Result.fail(HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "依赖服务暂时不可用，请稍后重试", "UPSTREAM_ERROR"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<?>> handleRuntimeException(RuntimeException ex) {
        if (ex instanceof IllegalArgumentException) {
            String message = ex.getMessage();
            return ResponseEntity.badRequest().body(Result.fail(400, message,
                    errorCodeFromMessage(message, "INVALID_ARGUMENT")));
        }
        Result<?> result = Result.error(ex.getMessage());
        result.setErrorCode("INTERNAL_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception ex) {
        Result<?> result = Result.error("Internal server error");
        result.setErrorCode("INTERNAL_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    private String errorCodeFromMessage(String message, String fallback) {
        if (message == null) return fallback;
        int separator = message.indexOf(':');
        if (separator <= 0) return fallback;
        String candidate = message.substring(0, separator).trim();
        return switch (candidate) {
            case "IDENTITY_NOT_BOUND", "INVALID_CONTEXT", "REPLAY_DETECTED", "NOT_FOUND", "IDENTITY_CONFLICT",
                    "IDEMPOTENCY_CONFLICT", "BINDING_REVOKED", "FORBIDDEN", "RATE_LIMITED", "UPSTREAM_ERROR",
                    "UPSTREAM_TIMEOUT" -> candidate;
            default -> fallback;
        };
    }
}
