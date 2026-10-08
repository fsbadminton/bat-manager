package com.fsb.service;

import com.fsb.Mapper.DashboardMapper;
import com.fsb.Service.impl.DashboardServiceImpl;
import com.fsb.pojo.DTO.OrderChartDTO;
import com.fsb.pojo.VO.DashboardSummaryVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private DashboardMapper dashboardMapper;

    @InjectMocks
    private DashboardServiceImpl service;

    @Test
    void returnsDashboardSummaryFromMapper() {
        DashboardSummaryVO summary = new DashboardSummaryVO();
        when(dashboardMapper.getSummary()).thenReturn(summary);

        assertSame(summary, service.getSummary());
        verify(dashboardMapper).getSummary();
    }

    @Test
    void returnsOrderChartForRequestedRange() {
        List<OrderChartDTO> rows = List.of(new OrderChartDTO());
        when(dashboardMapper.getOrderChart("2026-10-01", "2026-10-08")).thenReturn(rows);

        assertSame(rows, service.getOrderChart("2026-10-01", "2026-10-08"));
        verify(dashboardMapper).getOrderChart("2026-10-01", "2026-10-08");
    }
}
