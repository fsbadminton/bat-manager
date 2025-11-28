package com.fsb.Mapper;


import com.fsb.pojo.DTO.OrderChartDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DashboardMapper {

    // 折线图
    List<OrderChartDTO> getOrderChart(@Param("start") String start, @Param("end") String end);
}
