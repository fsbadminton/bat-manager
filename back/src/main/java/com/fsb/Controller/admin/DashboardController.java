package com.fsb.Controller.admin;


import com.fsb.Service.DashboardService;
import com.fsb.pojo.DTO.OrderChartDTO;
import com.fsb.pojo.VO.DashboardSummaryVO;
import com.fsb.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
public class DashboardController {


    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/order-chart")
    public Result<List<OrderChartDTO>> getOrderChart(@RequestParam String start, @RequestParam String end) {
        return Result.success(dashboardService.getOrderChart(start, end));
    }

    @GetMapping("/summary")
    public Result<DashboardSummaryVO> getSummary() {
        return Result.success(dashboardService.getSummary());
    }

}
