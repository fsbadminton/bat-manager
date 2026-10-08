package com.fsb.Service;

import com.fsb.pojo.DTO.OrderChartDTO;
import com.fsb.pojo.VO.DashboardSummaryVO;

import java.util.List;

public interface DashboardService {
    List<OrderChartDTO> getOrderChart(String start, String end);

    DashboardSummaryVO getSummary();
}
