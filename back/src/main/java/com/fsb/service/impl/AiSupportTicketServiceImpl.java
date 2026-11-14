package com.fsb.service.impl;

import com.fsb.Mapper.SupportTicketMapper;
import com.fsb.pojo.DTO.AiTicketCreateRequest;
import com.fsb.pojo.VO.AiTicketVO;
import com.fsb.pojo.entity.SupportTicket;
import com.fsb.Service.AiSupportTicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

@Service
public class AiSupportTicketServiceImpl implements AiSupportTicketService {
    private final SupportTicketMapper ticketMapper;

    public AiSupportTicketServiceImpl(SupportTicketMapper ticketMapper) {
        this.ticketMapper = ticketMapper;
    }

    @Override
    @Transactional
    public AiTicketVO create(Long userId, AiTicketCreateRequest request) {
        SupportTicket existing = ticketMapper.findByIdempotencyKey(userId, request.getIdempotencyKey());
        if (existing != null) {
            verifySameRequest(existing, request);
            return toView(existing);
        }

        SupportTicket ticket = new SupportTicket();
        ticket.setTicketNo("TK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        ticket.setUserId(userId);
        ticket.setTitle(request.getTitle().trim());
        ticket.setDescription(request.getDescription().trim());
        ticket.setPriority(request.getPriority() == null ? "medium" : request.getPriority());
        ticket.setIdempotencyKey(request.getIdempotencyKey());
        ticketMapper.insert(ticket);
        // The unique key and atomic insert decide the winner. Always re-read by
        // the scoped idempotency key so retries behave identically across JDBC drivers.
        SupportTicket createdOrRetried = ticketMapper.findByIdempotencyKey(userId, request.getIdempotencyKey());
        if (createdOrRetried == null) throw new IllegalStateException("Created support ticket could not be read");
        verifySameRequest(createdOrRetried, request);
        return toView(createdOrRetried);
    }

    @Override
    public List<AiTicketVO> list(Long userId, int limit) {
        if (limit < 1 || limit > 20) throw new IllegalArgumentException("limit must be between 1 and 20");
        return ticketMapper.listByUser(userId, limit).stream().map(this::toView).toList();
    }

    @Override
    public AiTicketVO get(Long userId, String ticketNo) {
        SupportTicket ticket = ticketMapper.findByTicketNo(userId, ticketNo);
        if (ticket == null) throw new com.fsb.exception.AuthException(404, "NOT_FOUND: ticket not found");
        return toView(ticket);
    }

    private AiTicketVO toView(SupportTicket ticket) {
        AiTicketVO result = new AiTicketVO();
        result.setId(ticket.getId());
        result.setTicketNo(ticket.getTicketNo());
        result.setTitle(ticket.getTitle());
        result.setDescription(ticket.getDescription());
        result.setPriority(ticket.getPriority());
        result.setStatus(ticket.getStatus());
        result.setAdminReply(ticket.getAdminReply());
        result.setCreatedAt(ticket.getCreatedAt());
        result.setUpdatedAt(ticket.getUpdatedAt());
        return result;
    }

    private void verifySameRequest(SupportTicket ticket, AiTicketCreateRequest request) {
        String priority = request.getPriority() == null ? "medium" : request.getPriority();
        if (!Objects.equals(ticket.getTitle(), request.getTitle().trim())
                || !Objects.equals(ticket.getDescription(), request.getDescription().trim())
                || !Objects.equals(ticket.getPriority(), priority)) {
            throw new com.fsb.exception.AuthException(409, "IDEMPOTENCY_CONFLICT: key reused with different ticket content");
        }
    }
}
