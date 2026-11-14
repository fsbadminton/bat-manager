package com.fsb.pojo.VO;

import com.fsb.pojo.entity.AiMessage;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiMessageVO {
    private Long id;
    private String channel;
    private String direction;
    private String contentType;
    private String content;
    private String status;
    private Long sequenceNo;
    private String modelName;
    private String errorCode;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static AiMessageVO from(AiMessage source) {
        AiMessageVO result = new AiMessageVO();
        result.id = source.getId();
        result.channel = source.getChannel();
        result.direction = source.getDirection();
        result.contentType = source.getContentType();
        result.content = source.getContent();
        result.status = source.getStatus();
        result.sequenceNo = source.getSequenceNo();
        result.modelName = source.getModelName();
        result.errorCode = source.getErrorCode();
        result.createdAt = source.getCreatedAt();
        result.completedAt = source.getCompletedAt();
        return result;
    }
}
