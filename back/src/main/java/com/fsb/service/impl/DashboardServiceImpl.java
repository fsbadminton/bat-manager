package com.fsb.Service.impl;

import com.fsb.Mapper.DashboardMapper;
import com.fsb.Service.DashboardService;
import com.fsb.pojo.DTO.OrderChartDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;



    /**
     * 获取订单图表数据
     * @param start
     * @param end
     * @return
     */
    public List<OrderChartDTO> getOrderChart(String start, String end) {
        return dashboardMapper.getOrderChart(start, end);
    }
}
