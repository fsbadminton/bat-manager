package com.fsb.Service.impl;

import com.fsb.pojo.DTO.OrderDTO;
import com.fsb.pojo.DTO.OrderItemDTO;
import com.fsb.pojo.DTO.OrderPageQueryDTO;
import com.fsb.pojo.VO.OrderItemVO;
import com.fsb.pojo.VO.OrderVO;
import com.fsb.pojo.entity.Customer;
import com.fsb.pojo.entity.Order;
import com.fsb.pojo.entity.OrderItem;
import com.fsb.pojo.entity.Racket;
import com.fsb.Mapper.InventoryMapper;
import com.fsb.Mapper.OrderItemMapper;
import com.fsb.Mapper.OrderMapper;
import com.fsb.Mapper.RacketMapper;
import com.fsb.Service.OrderService;
import com.fsb.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;


    @Override
    public PageResult pageQuery(OrderPageQueryDTO orderPageQueryDTO) {
        PageHelper.startPage(orderPageQueryDTO.getPageNum(), orderPageQueryDTO.getPageSize());
        Page<Order> page = orderMapper.pageQuery(orderPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public OrderVO getById(Long id) {
        // 1. 查询订单
        Order order = orderMapper.getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 查询订单项（直接用订单 id）
        List<OrderItem> orderItems = orderItemMapper.getOrderItemsByOrderId(String.valueOf(id));

        // 3. 封装返回对象
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderItems(orderItems);

        return orderVO;
    }

    @Override
    public void updateReceiverInfo(OrderDTO dto) {
        // 1. 查询订单
        Order order = orderMapper.getById(dto.getId());
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setAddress(dto.getAddress());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderMapper.getById(id);
        orderMapper.deleteById(id);
    }

    @Override
    public void updateAdminNote(OrderDTO dto) {
        Order order = orderMapper.getById(dto.getId());
        order.setAdminNote(dto.getAdminNote()); // 管理员备注
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void closeOrder(OrderDTO dto) {
        Order order = orderMapper.getById(dto.getId());
        order.setStatus(4); // 4 = 已关闭
        order.setNote(dto.getAdminNote());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }


    /**
     * 订单发货
     * @param orderId
     * @param deliveryCompany
     * @param deliverySn
     */
    @Override
    public void deliveryOrder(Long orderId, String deliveryCompany, String deliverySn) {
        Order order = orderMapper.getById(orderId);
        if(order == null) {
            throw new RuntimeException("订单不存在");
        }
        // 设置物流信息
        order.setDeliveryCompany(deliveryCompany);
        order.setDeliverySn(deliverySn);
        order.setDeliveryTime(LocalDateTime.now());
        // 修改订单状态为已发货
        order.setStatus(2); // 假设 2 = 已发货
        orderMapper.updateById(order);
    }
}
