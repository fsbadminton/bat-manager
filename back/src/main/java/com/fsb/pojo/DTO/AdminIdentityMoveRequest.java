package com.fsb.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminIdentityMoveRequest {
    @NotNull(message = "目标商城用户 ID 不能为空")
    private Long targetCustomerId;

    @NotBlank(message = "平台不能为空")
    private String platform;

    @NotBlank(message = "绑定引用不能为空")
    @Size(max = 128, message = "绑定引用过长")
    private String bindingReference;

    @NotBlank(message = "操作原因不能为空")
    @Size(max = 500, message = "操作原因不能超过 500 个字符")
    private String reason;
}
