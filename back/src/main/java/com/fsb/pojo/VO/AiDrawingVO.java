package com.fsb.pojo.VO;

import com.fsb.pojo.entity.AiDrawingRecord;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiDrawingVO {
    private String drawingId;
    private String prompt;
    private String modelName;
    private String provider;
    private String imageUrl;
    private String status;
    private Integer width;
    private Integer height;
    private String errorCode;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static AiDrawingVO from(AiDrawingRecord source) {
        AiDrawingVO result = new AiDrawingVO();
        result.drawingId = source.getDrawingId();
        result.prompt = source.getPrompt();
        result.modelName = source.getModelName();
        result.provider = source.getProvider();
        result.imageUrl = source.getImageUrl();
        result.status = source.getStatus();
        result.width = source.getWidth();
        result.height = source.getHeight();
        result.errorCode = source.getErrorCode();
        result.createdAt = source.getCreatedAt();
        result.completedAt = source.getCompletedAt();
        return result;
    }
}
