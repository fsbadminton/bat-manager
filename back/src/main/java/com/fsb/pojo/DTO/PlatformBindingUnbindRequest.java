package com.fsb.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlatformBindingUnbindRequest {
    @NotBlank(message = "平台不能为空")
    private String platform;

    @NotBlank(message = "绑定引用不能为空")
    @Size(max = 128, message = "绑定引用过长")
    private String bindingReference;
}
