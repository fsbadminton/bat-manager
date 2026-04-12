package com.fsb.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private Long id;
    private Long orderId;
    private String orderSn; // 订单编号 用这个


    private Long productId;
    private String productSn;
    private String productName;
    private String productPic;
    private String brandName;

    private BigDecimal productPrice;
    private Integer productQuantity;
    private BigDecimal productTotal;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
