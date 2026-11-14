package com.fsb.service;

import com.fsb.Mapper.SupportTicketMapper;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.AiTicketCreateRequest;
import com.fsb.pojo.entity.SupportTicket;
import com.fsb.service.impl.AiSupportTicketServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiSupportTicketServiceImplTest {
    @Test
    void retryWithSameKeyAndContentReturnsExistingTicket() {
        SupportTicketMapper mapper = mock(SupportTicketMapper.class);
        SupportTicket existing = ticket(7L, "TK-EXISTING", "same-key", "Need help", "Order issue", "medium");
        when(mapper.findByIdempotencyKey(7L, "same-key")).thenReturn(existing);
        AiSupportTicketServiceImpl service = new AiSupportTicketServiceImpl(mapper);

        var result = service.create(7L, request("same-key", "Need help", "Order issue", null));

        assertEquals("TK-EXISTING", result.getTicketNo());
        verify(mapper, never()).insert(any());
    }

    @Test
    void reusingKeyWithDifferentContentConflicts() {
        SupportTicketMapper mapper = mock(SupportTicketMapper.class);
        when(mapper.findByIdempotencyKey(7L, "same-key"))
                .thenReturn(ticket(7L, "TK-EXISTING", "same-key", "Need help", "Order issue", "medium"));
        AiSupportTicketServiceImpl service = new AiSupportTicketServiceImpl(mapper);

        AuthException error = assertThrows(AuthException.class,
                () -> service.create(7L, request("same-key", "Different issue", "Order issue", null)));

        assertEquals(409, error.getCode());
        verify(mapper, never()).insert(any());
    }

    @Test
    void lookupIsScopedToCurrentUser() {
        SupportTicketMapper mapper = mock(SupportTicketMapper.class);
        when(mapper.findByTicketNo(8L, "TK-OTHER")).thenReturn(null);
        AiSupportTicketServiceImpl service = new AiSupportTicketServiceImpl(mapper);

        AuthException error = assertThrows(AuthException.class, () -> service.get(8L, "TK-OTHER"));

        assertEquals(404, error.getCode());
        verify(mapper).findByTicketNo(eq(8L), eq("TK-OTHER"));
    }

    @Test
    void customerTicketViewIncludesPublicReplyButExcludesInternalNote() {
        SupportTicketMapper mapper = mock(SupportTicketMapper.class);
        SupportTicket ticket = ticket(7L, "TK-REPLY", "key-1", "Need help", "Order issue", "medium");
        ticket.setAdminReply("Please send the order number.");
        ticket.setAdminNote("Escalated internally.");
        when(mapper.findByTicketNo(7L, "TK-REPLY")).thenReturn(ticket);
        AiSupportTicketServiceImpl service = new AiSupportTicketServiceImpl(mapper);

        var result = service.get(7L, "TK-REPLY");

        assertEquals("Please send the order number.", result.getAdminReply());
        org.junit.jupiter.api.Assertions.assertFalse(result.toString().contains("Escalated internally."));
    }

    private AiTicketCreateRequest request(String key, String title, String description, String priority) {
        AiTicketCreateRequest request = new AiTicketCreateRequest();
        request.setIdempotencyKey(key);
        request.setTitle(title);
        request.setDescription(description);
        request.setPriority(priority);
        return request;
    }

    private SupportTicket ticket(Long userId, String ticketNo, String key, String title,
                                 String description, String priority) {
        SupportTicket ticket = new SupportTicket();
        ticket.setUserId(userId);
        ticket.setTicketNo(ticketNo);
        ticket.setIdempotencyKey(key);
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setPriority(priority);
        ticket.setStatus("open");
        return ticket;
    }
}
