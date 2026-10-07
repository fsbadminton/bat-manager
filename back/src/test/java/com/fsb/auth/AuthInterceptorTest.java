package com.fsb.auth;

import com.fsb.exception.AuthException;
import com.fsb.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                () -> new AuthInterceptor(jwtUtil).preHandle(request, null, null));
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
                () -> new AuthInterceptor(jwtUtil).preHandle(request, null, null));
        assertEquals(403, exception.getCode());
    }
}
