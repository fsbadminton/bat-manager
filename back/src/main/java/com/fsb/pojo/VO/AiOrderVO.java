package com.fsb.pojo.VO;

import com.fsb.pojo.entity.Order;
import com.fsb.pojo.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AiOrderVO {
    private Long id;
    private String orderSn;
    private BigDecimal totalAmount;
    private BigDecimal couponAmount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String deliveryCompany;
    private LocalDateTime deliveryTime;
    private Integer returnApplyStatus;
    private List<AiOrderItemVO> orderItems;

    public static AiOrderVO from(Order order) {
        AiOrderVO result = new AiOrderVO();
        result.setId(order.getId());
        result.setOrderSn(order.getOrderSn());
        result.setTotalAmount(order.getTotalAmount());
        result.setCouponAmount(order.getCouponAmount());
        result.setStatus(order.getStatus());
        result.setCreateTime(order.getCreateTime());
        result.setUpdateTime(order.getUpdateTime());
        result.setDeliveryCompany(order.getDeliveryCompany());
        result.setDeliveryTime(order.getDeliveryTime());
        result.setReturnApplyStatus(order.getReturnApplyStatus());
        List<OrderItem> items = order.getOrderItems();
        result.setOrderItems(items == null ? List.of() : items.stream().map(AiOrderItemVO::from).toList());
        return result;
    }

    public static AiOrderVO from(OrderVO order) {
        AiOrderVO result = new AiOrderVO();
        result.setId(order.getId());
        result.setOrderSn(order.getOrderSn());
        result.setTotalAmount(order.getTotalAmount());
        result.setCouponAmount(order.getCouponAmount());
        result.setStatus(order.getStatus());
        result.setCreateTime(order.getCreateTime());
        result.setUpdateTime(order.getUpdateTime());
        result.setDeliveryCompany(order.getDeliveryCompany());
        result.setDeliveryTime(order.getDeliveryTime());
        result.setReturnApplyStatus(order.getReturnApplyStatus());
        List<OrderItem> items = order.getOrderItems();
        result.setOrderItems(items == null ? List.of() : items.stream().map(AiOrderItemVO::from).toList());
        return result;
    }
}
