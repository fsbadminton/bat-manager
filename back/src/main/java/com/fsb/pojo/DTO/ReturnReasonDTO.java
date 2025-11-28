package com.fsb.pojo.DTO;

import lombok.Data;

@Data
public class ReturnReasonDTO {

    private Long id;

    private Long orderId;

    private Long userId;

    private String name;

    private String reason;
}
