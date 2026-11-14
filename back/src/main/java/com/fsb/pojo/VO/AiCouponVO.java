package com.fsb.pojo.VO;

import com.fsb.pojo.entity.CouponHistory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class AiCouponVO {
    private Long id;
    private String couponName;
    private BigDecimal amount;
    private BigDecimal minPoint;
    private Integer useStatus;
    private LocalDateTime createTime;
    private LocalDateTime useTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String orderSn;

    public static AiCouponVO from(CouponHistory source) {
        AiCouponVO result = new AiCouponVO();
        result.setId(source.getId());
        result.setCouponName(source.getCouponName());
        result.setAmount(source.getAmount());
        result.setMinPoint(source.getMinPoint());
        result.setUseStatus(source.getUseStatus());
        result.setCreateTime(source.getCreateTime());
        result.setUseTime(source.getUseTime());
        result.setStartTime(source.getStartTime());
        result.setEndTime(source.getEndTime());
        result.setOrderSn(source.getOrderSn());
        return result;
    }

    public static AiCouponVO from(Map<?, ?> source) {
        AiCouponVO result = new AiCouponVO();
        result.setId(asLong(source.get("id")));
        result.setCouponName(asString(source.get("couponName")));
        result.setAmount(asDecimal(source.get("amount")));
        result.setMinPoint(asDecimal(source.get("minPoint")));
        result.setUseStatus(asInteger(source.get("useStatus")));
        result.setCreateTime(asTime(source.get("createTime")));
        result.setUseTime(asTime(source.get("useTime")));
        result.setStartTime(asTime(source.get("startTime")));
        result.setEndTime(asTime(source.get("endTime")));
        result.setOrderSn(asString(source.get("orderSn")));
        return result;
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Long asLong(Object value) {
        if (value instanceof Number number) return number.longValue();
        try { return value == null ? null : Long.valueOf(String.valueOf(value)); }
        catch (NumberFormatException ignored) { return null; }
    }

    private static Integer asInteger(Object value) {
        if (value instanceof Number number) return number.intValue();
        try { return value == null ? null : Integer.valueOf(String.valueOf(value)); }
        catch (NumberFormatException ignored) { return null; }
    }

    private static BigDecimal asDecimal(Object value) {
        try { return value == null ? null : new BigDecimal(String.valueOf(value)); }
        catch (NumberFormatException ignored) { return null; }
    }

    private static LocalDateTime asTime(Object value) {
        if (value instanceof LocalDateTime time) return time;
        try { return value == null ? null : LocalDateTime.parse(String.valueOf(value)); }
        catch (RuntimeException ignored) { return null; }
    }
}
