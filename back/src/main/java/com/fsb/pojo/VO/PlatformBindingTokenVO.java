package com.fsb.pojo.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class PlatformBindingTokenVO {
    private String platform;
    private String token;
    private Instant expiresAt;
}
