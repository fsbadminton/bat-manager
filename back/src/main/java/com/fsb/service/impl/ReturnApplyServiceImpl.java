package com.fsb.Service.impl;

import com.fsb.Mapper.ReturnApplyMapper;
import com.fsb.Mapper.OrderMapper;
import com.fsb.Mapper.ReturnReasonMapper;
import com.fsb.Mapper.UserMapper;
import com.fsb.Service.ReturnApplyService;
import com.fsb.pojo.DTO.ReturnReasonDTO;
import com.fsb.pojo.VO.OmsReturnApplyVO;
import com.fsb.pojo.entity.OmsReturnApply;
import com.fsb.pojo.entity.Order;
import com.fsb.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class ReturnApplyServiceImpl implements ReturnApplyService {

    @Autowired
    private ReturnApplyMapper returnApplyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 用户创建退货申请
     * @param dto
     */
    @Override
    public void createReturnApply(ReturnReasonDTO dto) {
        Order order = orderMapper.getById(dto.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getMemberUsername() == null || !order.getMemberUsername().equals(dto.getName())) {
            throw new RuntimeException("无权申请该订单的退货");
        }
        Integer activeCount = returnApplyMapper.countActiveByOrderId(dto.getOrderId());
        if (activeCount != null && activeCount > 0) {
            throw new RuntimeException("该订单已有进行中的退货申请");
        }

        OmsReturnApply apply = new OmsReturnApply();
        apply.setOrderId(dto.getOrderId());
        apply.setUsername(dto.getName());
        apply.setReason(dto.getReason());
        apply.setStatus(0); // 待审核

        User user =userMapper.getByUsername(dto.getName());
        apply.setUserId(user.getId());

        apply.setCreateTime(LocalDateTime.now());

        returnApplyMapper.insertReturnApply(apply);
    }


    /**
     * 用户查询自己的退货申请列表
     * @param username
     * @return
     */
    @Override
    public List<OmsReturnApplyVO> getUserReturnApplyList(String username) {
        List<OmsReturnApplyVO> list = returnApplyMapper.listByUsername(username);
        for (OmsReturnApplyVO vo : list) {
            Order order = orderMapper.getById(vo.getOrderId());
            if (order != null) {
                vo.setOrder(order);
                vo.setOrderSn(order.getOrderSn());
                vo.setMemberUsername(order.getMemberUsername());
                vo.setReceiverName(order.getReceiverName());
                vo.setReceiverPhone(order.getReceiverPhone());
                vo.setAddress(order.getAddress());
                vo.setReturnAmount(order.getTotalAmount());
            }
        }
        return list;
    }
}
