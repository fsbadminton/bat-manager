package com.fsb.pojo.DTO;

import lombok.Data;

@Data
public class DeliveryOrderDTO {

    private Long orderId;           // 订单ID
    private String deliveryCompany; // 物流公司
    private String deliverySn;      // 物流单号
}
