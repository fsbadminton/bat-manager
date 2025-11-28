package com.fsb.Service.impl;


import com.fsb.Mapper.OmsReturnApplyMapper;
import com.fsb.Mapper.OrderItemMapper;
import com.fsb.Mapper.OrderMapper;
import com.fsb.Service.OmsReturnApplyService;
import com.fsb.pojo.DTO.OmsReturnApplyDTO;
import com.fsb.pojo.DTO.OmsReturnApplyPageQueryDTO;
import com.fsb.pojo.VO.OmsReturnApplyVO;
import com.fsb.pojo.entity.OmsReturnApply;
import com.fsb.pojo.entity.Order;
import com.fsb.pojo.entity.OrderItem;
import com.fsb.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class OmsReturnApplyServiceImpl implements OmsReturnApplyService {

    @Autowired
    private OmsReturnApplyMapper omsReturnApplyMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;


    @Override
    public PageResult pageQuery(OmsReturnApplyPageQueryDTO omsReturnApplyPageQueryDTO) {
        PageHelper.startPage(omsReturnApplyPageQueryDTO.getPageNum(), omsReturnApplyPageQueryDTO.getPageSize());
        // 添加空值检查
        if (omsReturnApplyPageQueryDTO.getCreateTime() != null) {
            LocalDateTime start = omsReturnApplyPageQueryDTO.getCreateTime().atStartOfDay();
            LocalDateTime end = omsReturnApplyPageQueryDTO.getCreateTime().atTime(23, 59, 59);
            omsReturnApplyPageQueryDTO.setStartTime(start);
            omsReturnApplyPageQueryDTO.setEndTime(end);
        }

        if (omsReturnApplyPageQueryDTO.getHandleTime() != null) {
            LocalDateTime start1 = omsReturnApplyPageQueryDTO.getHandleTime().atStartOfDay();
            LocalDateTime end1 = omsReturnApplyPageQueryDTO.getHandleTime().atTime(23, 59, 59);
            omsReturnApplyPageQueryDTO.setStartTime1(start1);
            omsReturnApplyPageQueryDTO.setEndTime1(end1);
        }

        Page<OmsReturnApply> page = omsReturnApplyMapper.pageQuery(omsReturnApplyPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }


    /**
     * 更新退货申请状态
     *
     * @param id
     * @param dto
     */
    @Override
    public void updateStatus(Long id, OmsReturnApplyDTO dto) {
        OmsReturnApply apply = omsReturnApplyMapper.getById(id);
        if (apply == null) {
            throw new RuntimeException("退货申请不存在");
        }

        apply.setStatus(dto.getStatus());
        apply.setHandleMan("连戍权");
        apply.setHandleTime(LocalDateTime.now());
        apply.setCompanyAddress("福州商家退货中心");
        apply.setId(id);
        log.info("更新退货申请状态：{}", apply);
        // 当状态为已收货或处理中时，设置收获时间
        if (dto.getStatus() == 2||dto.getStatus()==1) { // 假设2表示已收货
            apply.setReceiveTime(LocalDateTime.now());
        }
        // 如果你表里没有 note 字段，可以不写
        omsReturnApplyMapper.updateById(apply);
    }


    /**
     * 获取退货申请详情
     *
     * @param id
     * @return
     */
    @Override
    public OmsReturnApplyVO getDetail(Long id) {
        OmsReturnApply apply = omsReturnApplyMapper.getById(id);
        if (apply == null) {
            throw new RuntimeException("退货申请不存在");
        }

        OmsReturnApplyVO vo = new OmsReturnApplyVO();
        BeanUtils.copyProperties(apply, vo);

        //查订单
        Order order = orderMapper.getById(apply.getOrderId());
        if (order != null) {
            vo.setOrder( order);
        }
        vo.setReturnAmount(order.getTotalAmount());


        //查订单详情
        List<OrderItem> items = orderItemMapper.getOrderItemsByOrderId(String.valueOf(apply.getOrderId()));
        vo.setOrderItems(items);
        return vo;
    }
}
