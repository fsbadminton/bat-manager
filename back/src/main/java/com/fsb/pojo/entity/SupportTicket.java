package com.fsb.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupportTicket {
    private Long id;
    private String ticketNo;
    private Long userId;
    private String title;
    private String description;
    private String priority;
    private String status;
    private String idempotencyKey;
    private String adminNote;
    private String adminReply;
    private String handledBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
