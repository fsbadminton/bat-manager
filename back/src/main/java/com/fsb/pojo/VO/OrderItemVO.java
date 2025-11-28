package com.fsb.pojo.VO;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {

    private Long id;
    private Long productId;

    private String productName;
    private String productPic;
    private String brandName;
    private String productSn;

    private BigDecimal productPrice;
    private Integer productQuantity;
    private BigDecimal productTotal;
}
