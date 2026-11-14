package com.fsb.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlatformBindingStartRequest {
    @NotBlank(message = "平台不能为空")
    private String platform;
}
