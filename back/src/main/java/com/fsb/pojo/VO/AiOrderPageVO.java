package com.fsb.pojo.VO;

import com.fsb.pojo.entity.Order;
import com.fsb.result.PageResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiOrderPageVO {
    private long total;
    private List<AiOrderVO> records;

    public static AiOrderPageVO from(PageResult page) {
        AiOrderPageVO result = new AiOrderPageVO();
        result.setTotal(page.getTotal());
        List<AiOrderVO> records = new ArrayList<>();
        for (Object item : page.getRecords()) {
            if (item instanceof Order order) {
                records.add(AiOrderVO.from(order));
            }
        }
        result.setRecords(records);
        return result;
    }
}
