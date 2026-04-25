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
public class Coupon {
    private Long id;
    private Integer type;
    private String name;
    private Integer platform;
    private Integer publishCount;
    private BigDecimal amount;
    private Integer perLimit;
    private BigDecimal minPoint;
    private LocalDateTime enableTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer useType;
    private String note;
    private String productRelationJson;
    private String productCategoryRelationJson;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer receiveCount;
    private Integer useCount;
    private List<Map<String, Object>> productRelationList;
    private List<Map<String, Object>> productCategoryRelationList;
}
