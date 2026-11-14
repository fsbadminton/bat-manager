package com.fsb.pojo.VO;

import com.fsb.pojo.entity.Product;
import com.fsb.result.PageResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiProductPageVO {
    private long total;
    private List<AiProductVO> records;

    public static AiProductPageVO from(PageResult page) {
        AiProductPageVO result = new AiProductPageVO();
        result.setTotal(page.getTotal());
        List<AiProductVO> records = new ArrayList<>();
        for (Object item : page.getRecords()) {
            if (item instanceof Product product) records.add(AiProductVO.from(product));
        }
        result.setRecords(records);
        return result;
    }
}
