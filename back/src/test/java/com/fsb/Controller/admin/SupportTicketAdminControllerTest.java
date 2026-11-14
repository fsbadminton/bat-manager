package com.fsb.Controller.admin;

import com.fsb.Mapper.SupportTicketMapper;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.AdminSupportTicketUpdateRequest;
import com.fsb.pojo.entity.SupportTicket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SupportTicketAdminControllerTest {
    @AfterEach
    void clearAuthContext() {
        AuthContext.clear();
    }

    @Test
    void nonAdminCannotListTickets() {
        SupportTicketMapper mapper = mock(SupportTicketMapper.class);
        AuthContext.setCurrentUser(AuthUser.builder().userId(9L).username("customer").role("USER").build());
        SupportTicketAdminController controller = new SupportTicketAdminController(mapper);

        AuthException error = assertThrows(AuthException.class, () -> controller.list(20));

        assertEquals(403, error.getCode());
        verify(mapper, never()).listAll(20);
    }

    @Test
    void adminCanUpdateTicketStatusAndInternalNote() {
        SupportTicketMapper mapper = mock(SupportTicketMapper.class);
        when(mapper.findAnyByTicketNo("TK-123")).thenReturn(new SupportTicket());
        when(mapper.updateStatus("TK-123", "in_progress", "Investigating",
                "We are checking the order.", "operator")).thenReturn(1);
        AuthContext.setCurrentUser(AuthUser.builder().userId(1L).username("operator").role("ADMIN").build());
        SupportTicketAdminController controller = new SupportTicketAdminController(mapper);
        AdminSupportTicketUpdateRequest request = new AdminSupportTicketUpdateRequest();
        request.setStatus("in_progress");
        request.setAdminNote("Investigating");
        request.setAdminReply("We are checking the order.");

        controller.update("TK-123", request);

        verify(mapper).updateStatus("TK-123", "in_progress", "Investigating",
                "We are checking the order.", "operator");
    }

    @Test
    void ticketListRequiresAdminBeforeQuery() {
        SupportTicketMapper mapper = mock(SupportTicketMapper.class);
        AuthContext.setCurrentUser(AuthUser.builder().userId(9L).username("customer").role("USER").build());
        SupportTicketAdminController controller = new SupportTicketAdminController(mapper);

        assertThrows(AuthException.class, () -> controller.list(0));
        verify(mapper, never()).listAll(org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void updateReturnsNotFoundWhenTicketDisappearsBeforeWrite() {
        SupportTicketMapper mapper = mock(SupportTicketMapper.class);
        when(mapper.findAnyByTicketNo("TK-123")).thenReturn(new SupportTicket());
        when(mapper.updateStatus("TK-123", "resolved", null, null, "operator")).thenReturn(0);
        AuthContext.setCurrentUser(AuthUser.builder().userId(1L).username("operator").role("ADMIN").build());
        SupportTicketAdminController controller = new SupportTicketAdminController(mapper);
        AdminSupportTicketUpdateRequest request = new AdminSupportTicketUpdateRequest();
        request.setStatus("resolved");

        AuthException error = assertThrows(AuthException.class, () -> controller.update("TK-123", request));

        assertEquals(404, error.getCode());
    }
}
