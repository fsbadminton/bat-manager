package com.fsb.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AiMessagePersistRequest {
    @NotBlank
    @Size(max = 64)
    private String conversationId;
    @NotBlank
    @Size(max = 128)
    private String clientMessageId;
    @NotBlank
    @Pattern(regexp = "web|telegram|qq")
    private String channel;
    @Size(max = 128)
    private String externalChatId;
    @Size(max = 128)
    private String externalMessageId;
    @NotBlank
    @Pattern(regexp = "user|assistant|system|tool")
    private String direction;
    @Pattern(regexp = "text|markdown|image|json")
    private String contentType = "text";
    @NotBlank
    @Size(max = 50000)
    private String content;
    @Pattern(regexp = "pending|completed|failed")
    private String status = "completed";
    @Size(max = 128)
    private String modelName;
    @Size(max = 4000)
    private String toolsUsed;
}
