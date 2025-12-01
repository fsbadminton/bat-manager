package com.fsb.Service;

import com.fsb.entity.OrderItem;

import java.util.List;

public interface OrderItemService {
    //查询所有订单明细
    public List<OrderItem> findAll();
    //根据id查询订单明细
    public OrderItem findById(int id);
    //添加订单明细
    public void add(OrderItem orderItem);
    //修改订单明细
    public void update(OrderItem orderItem);
    //删除订单明细
    public void delete(int id);
}
