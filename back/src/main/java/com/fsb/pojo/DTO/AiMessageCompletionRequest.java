package com.fsb.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AiMessageCompletionRequest {
    @NotBlank
    @Size(max = 50000)
    private String content;
    @Size(max = 128)
    private String modelName;
    @Size(max = 4000)
    private String toolsUsed;
}
