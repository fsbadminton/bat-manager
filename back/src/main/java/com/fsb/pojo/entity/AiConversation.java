package com.fsb.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiConversation {
    private Long id;
    private String conversationId;
    private Long userId;
    private String scene;
    private String status;
    private LocalDateTime lastMessageAt;
    private String lastChannel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
