package com.fsb.pojo.entity;

import lombok.Data;

@Data
public class IdentityBindingAudit {
    private Long id;
    private String operatorName;
    private String action;
    private Long customerId;
    private String platform;
    private String externalUserIdHash;
    private String reason;
    private String outcome;
}
