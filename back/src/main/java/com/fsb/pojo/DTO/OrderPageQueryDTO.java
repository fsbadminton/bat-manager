package com.fsb.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPageQueryDTO {
    private Integer pageNum;
    private Integer pageSize;
    private String orderSn;
    private String receiverKeyword; // 收货人姓名/手机号
    private Integer status;
    private Integer orderType;
    private Integer sourceType;
    private LocalDate createTime; // 前端日期选择器对应
}
