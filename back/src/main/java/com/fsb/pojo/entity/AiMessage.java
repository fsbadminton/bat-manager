package com.fsb.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiMessage {
    private Long id;
    private Long conversationId;
    private Long userId;
    private String channel;
    private String externalChatId;
    private String externalMessageId;
    private String clientMessageId;
    private String direction;
    private String contentType;
    private String content;
    private String status;
    private Long sequenceNo;
    private String modelName;
    private String toolsUsed;
    private String errorCode;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
