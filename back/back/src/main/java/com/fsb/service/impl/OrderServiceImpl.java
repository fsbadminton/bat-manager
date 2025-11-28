package com.fsb.service.impl;

import com.fsb.entity.Customer;
import com.fsb.entity.Order;
import com.fsb.entity.OrderItem;
import com.fsb.entity.Racket;
import com.fsb.mapper.InventoryMapper;
import com.fsb.mapper.OrderItemMapper;
import com.fsb.mapper.OrderMapper;
import com.fsb.mapper.RacketMapper;
import com.fsb.service.OrderService;
import com.fsb.service.RacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private RacketMapper racketMapper;
    @Autowired
    private InventoryMapper inventoryMapper;

    @Override
    public List<Map<String,Object>> findAll() {
        return orderMapper.findAll();
    }

    @Override
    public List<Order> findById(int id) {
        return orderMapper.findById(id);
    }

    @Override
    public void add(Order order) {
        orderMapper.add(order);
    }

    @Override
    public void update(Order order) {
        orderMapper.update(order);
    }

    @Override
    public boolean delete(int id) {
        return orderMapper.delete(id)>0;
    }

    // 客户下单
    @Override
    public boolean placeOrder(Customer customer, int racketID, int quantity) {
        Racket racket = racketMapper.findById(racketID);
        if (racket == null) {
            System.out.println("找不到该球拍");
            return false;
        }

        if(quantity > inventoryMapper.findByRacketId(racketID).getStock() ){
//            System.out.println("库存不足，下单失败，请联系管理员添加库存");
            return false;
        }

        double totalPrice = racket.getPrice() * quantity;

        // 生成订单
        Order order = new Order();
        order.setCustomerID(customer.getCustomerID());
        order.setTotal(totalPrice);
        order.setOrderDate(new java.util.Date());
        orderMapper.add(order); // 主键自增，无需手动赋值

        // 生成订单明细
        OrderItem item = new OrderItem();
        item.setOrderID(order.getOrderID()); // 使用刚创建订单的订单ID
        item.setRacketID(racketID);
        item.setQuantity(quantity);
        item.setPrice(racket.getPrice());
        orderItemMapper.add(item); // 主键自增，无需手动赋值

        // 扣除库存
        inventoryMapper.reduceStock(racketID, quantity);
        System.out.println("下单成功！订单号：" + order.getOrderID());
        return true;
    }
}
