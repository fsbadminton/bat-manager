package com.fsb.pojo.VO;

import com.fsb.pojo.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AiOrderItemVO {
    private Long productId;
    private String productName;
    private String brandName;
    private BigDecimal productPrice;
    private Integer productQuantity;
    private BigDecimal productTotal;

    public static AiOrderItemVO from(OrderItem item) {
        AiOrderItemVO result = new AiOrderItemVO();
        result.setProductId(item.getProductId());
        result.setProductName(item.getProductName());
        result.setBrandName(item.getBrandName());
        result.setProductPrice(item.getProductPrice());
        result.setProductQuantity(item.getProductQuantity());
        result.setProductTotal(item.getProductTotal());
        return result;
    }
}
