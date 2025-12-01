package com.fsb.pojo.DTO;

import lombok.Data;

@Data
public class UserOrderUpdateDTO {
    private Long id;                  // 订单ID
    private Integer quantity;         // 购买数量
    private String receiverName;      // 收货人
    private String receiverPhone;     // 手机号
    private String address;           // 收货地址
    private String note;              // 用户备注（可选）
}
