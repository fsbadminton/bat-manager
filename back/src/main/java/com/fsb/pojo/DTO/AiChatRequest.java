package com.fsb.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AiChatRequest {
    @Size(max = 128, message = "会话 ID 不能超过 128 个字符")
    @Pattern(regexp = "[A-Za-z0-9_-]*", message = "会话 ID 格式无效")
    private String conversationId;

    @NotBlank(message = "消息不能为空")
    @Size(max = 5000, message = "消息不能超过 5000 个字符")
    private String message;
}
