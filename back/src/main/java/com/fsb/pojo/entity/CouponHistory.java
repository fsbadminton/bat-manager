package com.fsb.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponHistory {
    private Long id;
    private Long couponId;
    private String couponCode;
    private String memberNickname;
    private String memberUsername;
    private Integer getType;
    private LocalDateTime createTime;
    private Integer useStatus;
    private LocalDateTime useTime;
    private Long orderId;
    private String orderSn;

    private String couponName;
    private BigDecimal amount;
    private BigDecimal minPoint;
    private Integer useType;
    private Integer platform;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String productRelationJson;
    private String productCategoryRelationJson;
    private List<Map<String, Object>> productRelationList;
    private List<Map<String, Object>> productCategoryRelationList;
}
