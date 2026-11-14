package com.fsb.pojo.VO;

import com.fsb.pojo.entity.CouponHistory;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class AiCouponPageVO {
    private long total;
    private List<AiCouponVO> records;

    public static AiCouponPageVO from(Map<?, ?> source) {
        AiCouponPageVO result = new AiCouponPageVO();
        Object total = source.get("total");
        result.setTotal(total instanceof Number number ? number.longValue() : 0L);
        List<AiCouponVO> records = new ArrayList<>();
        Object list = source.get("list");
        if (list instanceof List<?> values) {
            for (Object value : values) {
                if (value instanceof CouponHistory coupon) records.add(AiCouponVO.from(coupon));
                else if (value instanceof Map<?, ?> map) records.add(AiCouponVO.from(map));
            }
        }
        result.setRecords(records);
        return result;
    }
}
