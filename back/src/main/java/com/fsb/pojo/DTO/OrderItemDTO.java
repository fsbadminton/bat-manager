package com.fsb.pojo.DTO;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long orderId;
    private String orderSn;

    private Long productId;
    private String productName;
    private String productPic;
    private String brandName;

    private BigDecimal productPrice;
    private Integer productQuantity;
}
