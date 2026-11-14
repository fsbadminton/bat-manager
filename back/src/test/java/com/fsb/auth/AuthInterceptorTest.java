package com.fsb.auth;

import com.fsb.exception.AuthException;
import com.fsb.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthInterceptorTest {

    @Test
    void userTokenCannotAccessAdminEndpoint() {
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        AuthUser user = AuthUser.builder()
                .username("alice")
                .role("USER")
                .permissions(java.util.List.of())
                .build();
        Mockito.when(jwtUtil.parseToken("user-token")).thenReturn(user);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/admin/product/page");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer user-token");

        AuthException exception = assertThrows(AuthException.class,
                () -> new AuthInterceptor(jwtUtil, Mockito.mock(AiConcurrencyLimiter.class)).preHandle(request, null, null));
        assertEquals(403, exception.getCode());
    }

    @Test
    void adminRoleWithoutAdminAccessPermissionCannotAccessAdminEndpoint() {
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        AuthUser admin = AuthUser.builder()
                .username("operator")
                .role("ADMIN")
                .permissions(java.util.List.of())
                .build();
        Mockito.when(jwtUtil.parseToken("admin-token")).thenReturn(admin);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/admin/identity-bindings/42");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer admin-token");

        AuthException exception = assertThrows(AuthException.class,
                () -> new AuthInterceptor(jwtUtil, Mockito.mock(AiConcurrencyLimiter.class)).preHandle(request, null, null));
        assertEquals(403, exception.getCode());
    }

    @Test
    void adminTokenCannotAccessUserEndpoint() {
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        AuthUser admin = AuthUser.builder()
                .username("operator")
                .role("ADMIN")
                .permissions(java.util.List.of(PermissionConstants.ADMIN_ACCESS))
                .build();
        Mockito.when(jwtUtil.parseToken("admin-token")).thenReturn(admin);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/user/order/list");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer admin-token");

        AuthException exception = assertThrows(AuthException.class,
                () -> new AuthInterceptor(jwtUtil, Mockito.mock(AiConcurrencyLimiter.class)).preHandle(request, null, null));
        assertEquals(403, exception.getCode());
    }

    @Test
    void internalAiEndpointRejectsMissingServiceTokenConfiguration() {
        AuthInterceptor interceptor = new AuthInterceptor(Mockito.mock(JwtUtil.class), Mockito.mock(AiConcurrencyLimiter.class));
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/internal/ai/products");
        Mockito.when(request.getHeader("X-AI-Service-Token")).thenReturn("bat-manager-ai-local-token");
        ReflectionTestUtils.setField(interceptor, "aiInternalToken", "");

        AuthException exception = assertThrows(AuthException.class,
                () -> interceptor.preHandle(request, null, null));
        assertEquals(401, exception.getCode());
    }

    @Test
    void internalAiEndpointRejectsRequestsWhenSharedCapacityIsFull() {
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        AiConcurrencyLimiter limiter = Mockito.mock(AiConcurrencyLimiter.class);
        Mockito.when(limiter.tryAcquire()).thenReturn(null);
        AuthInterceptor interceptor = new AuthInterceptor(jwtUtil, limiter);
        ReflectionTestUtils.setField(interceptor, "aiInternalToken", "0123456789abcdef0123456789abcdef");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/internal/ai/products");
        Mockito.when(request.getHeader("X-AI-Service-Token")).thenReturn("0123456789abcdef0123456789abcdef");

        AuthException exception = assertThrows(AuthException.class,
                () -> interceptor.preHandle(request, null, null));
        assertEquals(429, exception.getCode());
        Mockito.verify(request, Mockito.never()).setAttribute(Mockito.eq("aiConcurrencyLease"), Mockito.any());
    }

    @Test
    void internalAiEndpointReleasesTheSharedLeaseAfterCompletion() {
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        AiConcurrencyLimiter limiter = Mockito.mock(AiConcurrencyLimiter.class);
        Mockito.when(limiter.tryAcquire()).thenReturn("lease-1");
        AuthInterceptor interceptor = new AuthInterceptor(jwtUtil, limiter);
        ReflectionTestUtils.setField(interceptor, "aiInternalToken", "0123456789abcdef0123456789abcdef");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/internal/ai/products");
        Mockito.when(request.getHeader("X-AI-Service-Token")).thenReturn("0123456789abcdef0123456789abcdef");
        Mockito.when(request.getAttribute("aiConcurrencyLease")).thenReturn("lease-1");

        org.junit.jupiter.api.Assertions.assertTrue(interceptor.preHandle(request, null, null));
        interceptor.afterCompletion(request, null, null, null);

        Mockito.verify(limiter).release("lease-1");
    }

    @Test
    void webAiEndpointAcquiresAndReleasesTheSharedLease() {
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        AiConcurrencyLimiter limiter = Mockito.mock(AiConcurrencyLimiter.class);
        Mockito.when(limiter.tryAcquire()).thenReturn("web-lease");
        AuthUser user = AuthUser.builder()
                .username("alice")
                .role("USER")
                .permissions(java.util.List.of())
                .build();
        Mockito.when(jwtUtil.parseToken("user-token")).thenReturn(user);
        AuthInterceptor interceptor = new AuthInterceptor(jwtUtil, limiter);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/user/ai/chat");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer user-token");
        Mockito.when(request.getAttribute("aiConcurrencyLease")).thenReturn("web-lease");

        assertTrue(interceptor.preHandle(request, null, null));
        interceptor.afterCompletion(request, null, null, null);

        Mockito.verify(request).setAttribute("aiConcurrencyLease", "web-lease");
        Mockito.verify(limiter).release("web-lease");
        org.junit.jupiter.api.Assertions.assertNull(AuthContext.getCurrentUser());
    }

    @Test
    void webAiEndpointRejectsWhenSharedCapacityIsFullAndClearsAuthContext() {
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        AiConcurrencyLimiter limiter = Mockito.mock(AiConcurrencyLimiter.class);
        Mockito.when(limiter.tryAcquire()).thenReturn(null);
        AuthUser user = AuthUser.builder()
                .username("alice")
                .role("USER")
                .permissions(java.util.List.of())
                .build();
        Mockito.when(jwtUtil.parseToken("user-token")).thenReturn(user);
        AuthInterceptor interceptor = new AuthInterceptor(jwtUtil, limiter);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/user/ai/chat");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer user-token");

        AuthException exception = assertThrows(AuthException.class,
                () -> interceptor.preHandle(request, null, null));

        assertEquals(429, exception.getCode());
        Mockito.verify(request, Mockito.never()).setAttribute(Mockito.eq("aiConcurrencyLease"), Mockito.any());
        org.junit.jupiter.api.Assertions.assertNull(AuthContext.getCurrentUser());
    }

    @Test
    void unauthenticatedWebAiRequestDoesNotAcquireALease() {
        AiConcurrencyLimiter limiter = Mockito.mock(AiConcurrencyLimiter.class);
        AuthInterceptor interceptor = new AuthInterceptor(Mockito.mock(JwtUtil.class), limiter);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/user/ai/chat");

        assertThrows(AuthException.class, () -> interceptor.preHandle(request, null, null));

        Mockito.verifyNoInteractions(limiter);
    }

    @Test
    void webAiLeaseStoreFailureClearsAuthContext() {
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        AiConcurrencyLimiter limiter = Mockito.mock(AiConcurrencyLimiter.class);
        Mockito.when(limiter.tryAcquire()).thenThrow(new IllegalStateException("Redis unavailable"));
        AuthUser user = AuthUser.builder()
                .username("alice")
                .role("USER")
                .permissions(java.util.List.of())
                .build();
        Mockito.when(jwtUtil.parseToken("user-token")).thenReturn(user);
        AuthInterceptor interceptor = new AuthInterceptor(jwtUtil, limiter);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/user/ai/chat");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer user-token");

        assertThrows(IllegalStateException.class, () -> interceptor.preHandle(request, null, null));

        org.junit.jupiter.api.Assertions.assertNull(AuthContext.getCurrentUser());
    }

    @Test
    void afterCompletionClearsAuthContextWhenLeaseReleaseFails() {
        AiConcurrencyLimiter limiter = Mockito.mock(AiConcurrencyLimiter.class);
        Mockito.doThrow(new IllegalStateException("Redis unavailable"))
                .when(limiter).release("lease-1");
        AuthInterceptor interceptor = new AuthInterceptor(Mockito.mock(JwtUtil.class), limiter);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getAttribute("aiConcurrencyLease")).thenReturn("lease-1");
        AuthContext.setCurrentUser(AuthUser.builder().username("alice").role("USER").build());

        assertThrows(IllegalStateException.class,
                () -> interceptor.afterCompletion(request, null, null, null));

        org.junit.jupiter.api.Assertions.assertNull(AuthContext.getCurrentUser());
    }
}
