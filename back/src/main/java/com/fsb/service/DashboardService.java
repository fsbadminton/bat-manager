package com.fsb.Service;

import com.fsb.pojo.DTO.OrderChartDTO;

import java.util.List;

public interface DashboardService {
    List<OrderChartDTO> getOrderChart(String start, String end);
}
