package com.fsb.Handler;

import com.fsb.exception.AuthException;
import com.fsb.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public Result<?> handleAuthException(AuthException ex) {
        return Result.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException ex) {
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception ex) {
        return Result.error("Internal server error");
    }
}
