package com.fsb.Service;

import com.fsb.entity.Customer;
import com.fsb.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {

    // 查询所有订单
    public List<Map<String,Object>> findAll();

    // 根据客户id查询订单
    public List<Order> findById(int id);

    // 添加订单
    public void add(Order order);

    // 修改订单
    public void update(Order order);

    // 删除订单
    public boolean delete(int id);

    //下单
    public boolean placeOrder(Customer customer, int racketID, int quantity);
}
