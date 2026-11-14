package com.fsb.pojo.VO;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

@Data
public class PlatformBindingVO {
    private String platform;
    private String bindingReference;
    @JsonIgnore
    private String externalUserId;
    private String status;
    private Long bindingVersion;
    private Instant boundAt;
}
