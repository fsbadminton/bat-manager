package com.fsb.auth;

import com.fsb.exception.AuthException;
import com.fsb.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final AiConcurrencyLimiter aiConcurrencyLimiter;

    @Value("${ai.internal-token:}")
    private String aiInternalToken;

    public AuthInterceptor(JwtUtil jwtUtil, AiConcurrencyLimiter aiConcurrencyLimiter) {
        this.jwtUtil = jwtUtil;
        this.aiConcurrencyLimiter = aiConcurrencyLimiter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();

        if (uri.startsWith("/internal/ai/")) {
            String suppliedToken = request.getHeader("X-AI-Service-Token");
            if (aiInternalToken == null || aiInternalToken.length() < 32
                    || suppliedToken == null
                    || !MessageDigest.isEqual(aiInternalToken.getBytes(StandardCharsets.UTF_8),
                    suppliedToken.getBytes(StandardCharsets.UTF_8))) {
                throw new AuthException(401, "Invalid AI service token");
            }
            String leaseId = aiConcurrencyLimiter.tryAcquire();
            if (leaseId == null) {
                throw new AuthException(429, "RATE_LIMITED: AI backend is at capacity");
            }
            request.setAttribute("aiConcurrencyLease", leaseId);
            return true;
        }

        if (isWhitelisted(uri)) {
            return true;
        }

        String rawToken = request.getHeader("Authorization");
        if (rawToken == null || rawToken.trim().isEmpty()) {
            throw new AuthException(401, "Unauthorized");
        }

        String token = normalizeToken(rawToken);
        AuthUser authUser;
        try {
            authUser = jwtUtil.parseToken(token);
        } catch (Exception ex) {
            throw new AuthException(401, "Invalid token");
        }

        AuthContext.setCurrentUser(authUser);
        request.setAttribute("authUser", authUser);

        try {
            if (uri.startsWith("/admin/")) {
                requireRole(authUser, "ADMIN", "Admin role required");
                requirePermission(authUser, PermissionConstants.ADMIN_ACCESS, "Admin permission required");
                return true;
            }

            if (uri.startsWith("/user/")) {
                requireRole(authUser, "USER", "User role required");
            }

            if (uri.startsWith("/user/ai/")) {
                acquireAiLease(request);
            }

            if ("/user/review/add".equals(uri)) {
                requirePermission(authUser, PermissionConstants.REVIEW_CREATE_OWN, "Review create permission required");
            } else if ("/user/review/update".equals(uri)) {
                requirePermission(authUser, PermissionConstants.REVIEW_UPDATE_OWN, "Review update permission required");
            } else if (uri.startsWith("/user/review/delete/")) {
                requirePermission(authUser, PermissionConstants.REVIEW_DELETE_OWN, "Review delete permission required");
            } else if ("/user/review/list".equals(uri)) {
                if (!authUser.hasPermission(PermissionConstants.REVIEW_READ_ANY)
                        && !authUser.hasPermission(PermissionConstants.REVIEW_READ_OWN)) {
                    throw new AuthException(403, "Review read permission required");
                }
            } else if ("/user/order/create".equals(uri)) {
                requirePermission(authUser, PermissionConstants.ORDER_CREATE_OWN, "Order create permission required");
            } else if ("/user/order/list".equals(uri)) {
                requirePermission(authUser, PermissionConstants.ORDER_READ_OWN, "Order read permission required");
            } else if ("/user/returnApply/create".equals(uri)) {
                requirePermission(authUser, PermissionConstants.REFUND_APPLY_OWN, "Refund apply permission required");
            } else if ("/user/returnApply/listAll".equals(uri) || "/user/returnApply/list".equals(uri)) {
                requirePermission(authUser, PermissionConstants.REFUND_READ_OWN, "Refund read permission required");
            }

            return true;
        } catch (RuntimeException ex) {
            AuthContext.clear();
            throw ex;
        }
    }

    private void acquireAiLease(HttpServletRequest request) {
        try {
            String leaseId = aiConcurrencyLimiter.tryAcquire();
            if (leaseId == null) {
                throw new AuthException(429, "RATE_LIMITED: AI backend is at capacity");
            }
            request.setAttribute("aiConcurrencyLease", leaseId);
        } catch (RuntimeException ex) {
            AuthContext.clear();
            throw ex;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            Object leaseId = request.getAttribute("aiConcurrencyLease");
            if (leaseId instanceof String value) {
                aiConcurrencyLimiter.release(value);
            }
        } finally {
            AuthContext.clear();
        }
    }

    private boolean isWhitelisted(String uri) {
        return "/admin/login".equals(uri)
                || "/user/login".equals(uri)
                || "/user/register".equals(uri)
                || "/user/sendCode".equals(uri)
                || "/error".equals(uri)
                || uri.startsWith("/auth/");
    }

    private String normalizeToken(String rawToken) {
        String token = rawToken.trim();
        if (token.toLowerCase().startsWith("bearer ")) {
            return token.substring(7).trim();
        }
        return token;
    }

    private void requirePermission(AuthUser authUser, String permission, String message) {
        if (authUser == null || !authUser.hasPermission(permission)) {
            throw new AuthException(403, message);
        }
    }

    private void requireRole(AuthUser authUser, String expectedRole, String message) {
        if (authUser == null || !expectedRole.equalsIgnoreCase(authUser.getRole())) {
            throw new AuthException(403, message);
        }
    }
}
