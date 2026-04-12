package com.fsb.Service.impl;

import com.fsb.Mapper.OrderItemMapper;
import com.fsb.Mapper.OrderMapper;
import com.fsb.Mapper.ProductMapper;
import com.fsb.Service.UserOrderService;
import com.fsb.pojo.DTO.OrderPageQueryDTO;
import com.fsb.pojo.DTO.UserOrderCreateDTO;
import com.fsb.pojo.entity.Order;
import com.fsb.pojo.entity.OrderItem;
import com.fsb.pojo.entity.Product;
import com.fsb.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserOrderServiceImpl implements UserOrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public Long createOrder(UserOrderCreateDTO dto, String username) {
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        if (product.getStock() == null || product.getStock() <= 0 || product.getStock() < dto.getQuantity()) {
            throw new RuntimeException("商品库存不足");
        }

        BigDecimal totalAmount = BigDecimal.valueOf(product.getPrice() * dto.getQuantity());

        Order order = new Order();
        order.setMemberUsername(username);
        order.setTotalAmount(totalAmount);
        order.setPayType(1);
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setAddress(dto.getAddress());
        order.setSourceType(1);
        order.setStatus(1);
        order.setOrderType(1);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setOrderSn(generateOrderSn());
        orderMapper.insert(order);

        OrderItem item = new OrderItem();
        item.setOrderSn(order.getOrderSn());
        item.setOrderId(order.getId());
        item.setProductId(product.getProductId());
        item.setProductName(product.getName());
        item.setProductPrice(BigDecimal.valueOf(product.getPrice()));
        item.setProductQuantity(dto.getQuantity());
        item.setProductTotal(totalAmount);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        orderItemMapper.insert(item);

        productMapper.reduceStock(dto.getProductId(), dto.getQuantity());
        productMapper.increaseSales(dto.getProductId(), dto.getQuantity());
        return order.getId();
    }

    @Override
    public PageResult listUserOrders(String username, OrderPageQueryDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        Page<Order> page = orderMapper.listUserPageQuery(username, dto);

        List<Order> orders = page.getResult();
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemMapper.getOrderItemsByOrderId(String.valueOf(order.getId()));
            order.setOrderItems(orderItems);
        }

        return new PageResult(page.getTotal(), orders);
    }

    private String generateOrderSn() {
        return "ORDER" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
