package com.fsb.pojo.DTO;

import lombok.Data;

@Data
public class UserCouponPageQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private Integer useStatus;
}
