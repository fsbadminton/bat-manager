package com.fsb.service.impl;

import com.fsb.entity.OrderItem;
import com.fsb.mapper.OrderItemMapper;
import com.fsb.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItem> findAll() {
        return orderItemMapper.findAll();
    }

    @Override
    public OrderItem findById(int id) {
        return orderItemMapper.findById(id);
    }

    @Override
    public void add(OrderItem orderItem) {
        orderItemMapper.add(orderItem);
    }

    @Override
    public void update(OrderItem orderItem) {
        orderItemMapper.update(orderItem);
    }

    @Override
    public void delete(int id) {
        orderItemMapper.delete(id);
    }
}
