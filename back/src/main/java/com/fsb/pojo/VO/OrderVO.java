package com.fsb.pojo.VO;

import com.fsb.pojo.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVO {
    private Long id;
    private String orderSn;
    private String memberUsername;
    private BigDecimal totalAmount;
    private Integer payType;
    private Integer sourceType;
    private Integer status;
    private Integer orderType;
    private String receiverName;
    private String receiverPhone;
    private String receiverPostCode;
    private String receiverProvince;
    private String receiverCity;
    private String receiverRegion;
    private String receiverDetailAddress;
    private String address;
    private String note;
    private String adminNote;  // 管理员备注
    private String deliveryCompany;
    private String deliverySn;
    private LocalDateTime createTime;
    private Integer returnApplyStatus;
    private Long returnApplyId;

    // 订单商品明细列表
    private List<OrderItem> orderItems;
}
