package com.fsb.pojo.VO;

import com.fsb.pojo.entity.AiConversation;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AiConversationVO {
    private String conversationId;
    private Long userId;
    private String scene;
    private String status;
    private String lastChannel;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AiMessageVO> messages;

    public static AiConversationVO from(AiConversation source, List<AiMessageVO> messages) {
        AiConversationVO result = new AiConversationVO();
        result.conversationId = source.getConversationId();
        result.userId = source.getUserId();
        result.scene = source.getScene();
        result.status = source.getStatus();
        result.lastChannel = source.getLastChannel();
        result.lastMessageAt = source.getLastMessageAt();
        result.createdAt = source.getCreatedAt();
        result.updatedAt = source.getUpdatedAt();
        result.messages = messages;
        return result;
    }
}
