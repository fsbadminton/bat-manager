package com.fsb.pojo.VO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardSummaryVO {
    private Long todayOrderCount;
    private BigDecimal todaySalesAmount;
    private BigDecimal yesterdaySalesAmount;
    private Long pendingPaymentCount;
    private Long pendingDeliveryCount;
    private Long shippedCount;
    private Long completedCount;
    private Long pendingReturnCount;
    private Long totalProductCount;
    private Long publishedProductCount;
    private Long unpublishedProductCount;
    private Long lowStockProductCount;
    private Long totalUserCount;
    private Long todayUserCount;
    private Long yesterdayUserCount;
    private Long monthUserCount;
    private Long monthOrderCount;
    private Long weekOrderCount;
    private BigDecimal monthSalesAmount;
    private BigDecimal weekSalesAmount;
}
