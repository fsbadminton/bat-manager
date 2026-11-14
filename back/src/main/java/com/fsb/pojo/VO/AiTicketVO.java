package com.fsb.pojo.VO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiTicketVO {
    private Long id;
    private String ticketNo;
    private String title;
    private String description;
    private String priority;
    private String status;
    private String adminReply;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
