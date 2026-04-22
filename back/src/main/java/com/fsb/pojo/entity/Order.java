package com.fsb.pojo.entity;

import com.fsb.pojo.VO.OrderItemVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long id;
    private String orderSn;
    private String memberUsername;
    private BigDecimal totalAmount;
    private Integer payType; // 0未支付 1已支付 2已支付
    private Integer sourceType; // 0 PC订单 1 APP订单
    private Integer status; // 0待付款 1待发货 2已发货 3已完成 4已关闭 5无效订单
    private Integer orderType; // 0正常订单 1秒杀订单
    private String receiverName;
    private String receiverPhone;
    private String address;
    private String receiverPostCode;
    private String receiverProvince;
    private String receiverCity;
    private String receiverRegion;
    private String receiverDetailAddress;
    private String note; // 用户备注
    private String adminNote;  // 管理员备注
    private String deliveryCompany;
    private String deliverySn;
    private LocalDateTime deliveryTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer returnApplyStatus;
    private Long returnApplyId;

    // 订单商品明细列表
    private List<OrderItem> orderItems;
}
