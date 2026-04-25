package com.fsb.pojo.DTO;

import lombok.Data;

@Data
public class CouponHistoryPageQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private Integer useStatus;
    private String orderSn;
    private Long couponId;
}
