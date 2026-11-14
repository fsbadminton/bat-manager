package com.fsb.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlatformBindingConfirmRequest {
    @NotBlank(message = "平台不能为空")
    private String platform;

    @NotBlank(message = "外部用户 ID 不能为空")
    @Size(max = 256, message = "外部用户 ID 过长")
    private String externalUserId;

    @NotBlank(message = "绑定凭证不能为空")
    @Size(max = 128, message = "绑定凭证无效")
    private String token;

    @NotBlank(message = "幂等键不能为空")
    @Size(max = 100, message = "幂等键过长")
    private String idempotencyKey;
}
