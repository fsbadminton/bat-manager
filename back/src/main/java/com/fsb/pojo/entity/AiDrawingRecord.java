package com.fsb.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiDrawingRecord {
    private Long id;
    private String drawingId;
    private Long userId;
    private Long conversationId;
    private Long messageId;
    private String prompt;
    private String modelName;
    private String provider;
    private String objectKey;
    private String imageUrl;
    private String status;
    private Integer width;
    private Integer height;
    private String errorCode;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
