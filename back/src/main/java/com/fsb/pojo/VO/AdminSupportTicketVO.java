package com.fsb.pojo.VO;

import com.fsb.pojo.entity.SupportTicket;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminSupportTicketVO {
    private Long userId;
    private String ticketNo;
    private String title;
    private String description;
    private String priority;
    private String status;
    private String adminNote;
    private String adminReply;
    private String handledBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AdminSupportTicketVO from(SupportTicket ticket) {
        AdminSupportTicketVO result = new AdminSupportTicketVO();
        result.setUserId(ticket.getUserId());
        result.setTicketNo(ticket.getTicketNo());
        result.setTitle(ticket.getTitle());
        result.setDescription(ticket.getDescription());
        result.setPriority(ticket.getPriority());
        result.setStatus(ticket.getStatus());
        result.setAdminNote(ticket.getAdminNote());
        result.setAdminReply(ticket.getAdminReply());
        result.setHandledBy(ticket.getHandledBy());
        result.setCreatedAt(ticket.getCreatedAt());
        result.setUpdatedAt(ticket.getUpdatedAt());
        return result;
    }
}
