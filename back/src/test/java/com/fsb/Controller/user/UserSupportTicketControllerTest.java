package com.fsb.Controller.user;

import com.fsb.Service.AiSupportTicketService;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.pojo.DTO.AiTicketCreateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class UserSupportTicketControllerTest {
    @AfterEach
    void clearAuthContext() {
        AuthContext.clear();
    }

    @Test
    void listUsesAuthenticatedCustomerAndValidatesLimit() {
        AiSupportTicketService tickets = mock(AiSupportTicketService.class);
        UserSupportTicketController controller = new UserSupportTicketController(tickets);
        AuthContext.setCurrentUser(AuthUser.builder().userId(42L).username("alice").role("USER").build());

        assertEquals(1, controller.list(20).getCode());
        verify(tickets).list(42L, 20);
        assertThrows(IllegalArgumentException.class, () -> controller.list(21));
        verify(tickets, never()).list(42L, 21);
    }

    @Test
    void ticketDetailUsesAuthenticatedCustomerScope() {
        AiSupportTicketService tickets = mock(AiSupportTicketService.class);
        UserSupportTicketController controller = new UserSupportTicketController(tickets);
        AuthContext.setCurrentUser(AuthUser.builder().userId(42L).username("alice").role("USER").build());

        controller.detail("TK-123");

        verify(tickets).get(42L, "TK-123");
    }

    @Test
    void createUsesAuthenticatedCustomerAndRequestIdempotencyKey() {
        AiSupportTicketService tickets = mock(AiSupportTicketService.class);
        UserSupportTicketController controller = new UserSupportTicketController(tickets);
        AuthContext.setCurrentUser(AuthUser.builder().userId(42L).username("alice").role("USER").build());
        AiTicketCreateRequest request = new AiTicketCreateRequest();
        request.setTitle("Order problem");
        request.setDescription("I need help with this order.");
        request.setPriority("high");
        request.setIdempotencyKey("web-request-123");

        controller.create(request);

        verify(tickets).create(42L, request);
    }
}
