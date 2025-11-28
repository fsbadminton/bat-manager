package com.fsb.pojo.DTO;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderChartDTO {

    private String date;       // yyyy-MM-dd
    private Integer orderCount;
    private BigDecimal orderAmount;
}
