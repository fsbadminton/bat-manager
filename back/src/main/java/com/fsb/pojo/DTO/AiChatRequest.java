package com.fsb.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiChatRequest {
    private String conversationId;

    @NotBlank(message = "消息不能为空")
    private String message;
}
