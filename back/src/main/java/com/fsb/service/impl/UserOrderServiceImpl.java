package com.fsb.Service.impl;

import com.fsb.Mapper.OrderItemMapper;
import com.fsb.Mapper.OrderMapper;
import com.fsb.Mapper.ProductMapper;
import com.fsb.Service.UserOrderService;
import com.fsb.pojo.DTO.OrderPageQueryDTO;
import com.fsb.pojo.DTO.UserOrderCreateDTO;
import com.fsb.pojo.DTO.UserOrderUpdateDTO;
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


    /**
     * 创建订单
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public Long createOrder(UserOrderCreateDTO dto) {

        // 1. 查询商品
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 检查库存是否充足
        if (product.getStock() < dto.getQuantity()||product.getStock() == null || product.getStock() <= 0) {
            throw new RuntimeException("商品库存不足");
        }

        // 2. 计算金额
        BigDecimal totalAmount = BigDecimal.valueOf(product.getPrice()*dto.getQuantity());

        // 3. 保存订单主表
        Order order = new Order();
        order.setMemberUsername("zhangsan");  // 你后面可以从 token 中取
        order.setTotalAmount(totalAmount);
        order.setPayType(1);
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setAddress(dto.getAddress());
        order.setSourceType(1);
        order.setStatus(1); // 待支付
        order.setOrderType(1);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        // 添加订单编号
        order.setOrderSn(generateOrderSn()); // 或者使用其他方式生成唯一订单号
        orderMapper.insert(order);

        // 4. 保存订单明细
        OrderItem item = new OrderItem();
        item.setOrderSn(order.getOrderSn());
        item.setOrderId(order.getId());
        item.setProductId(product.getProductId());
        item.setProductName(product.getName());
        item.setProductPic(product.getImageUrl());
        item.setBrandName(product.getBrandName());
        item.setProductPrice(BigDecimal.valueOf(product.getPrice()));
        item.setProductQuantity(dto.getQuantity());
        item.setProductTotal(totalAmount);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        orderItemMapper.insert(item);

        // 5. 减少商品库存
        productMapper.reduceStock(dto.getProductId(), dto.getQuantity());

        //6.增加商品销量
        productMapper.increaseSales(dto.getProductId(),dto.getQuantity());
        return order.getId();
    }

    /**
     * 分页查询用户本人订单
     * @param username
     * @param dto
     * @return
     */
    @Override
    public PageResult listUserOrders(String username, OrderPageQueryDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        Page<Order> page = orderMapper.listUserPageQuery(username,dto);

        List<Order> orders = page.getResult();
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemMapper.getOrderItemsByOrderId(String.valueOf(order.getId()));
            order.setOrderItems(orderItems);
        }

        return new PageResult(page.getTotal(), orders);

    }

    @Override
    public void updateUserOrder(UserOrderUpdateDTO dto) {
        // 1. 查询订单
        Order order = orderMapper.getById(dto.getId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 更新基本信息
        if (dto.getReceiverName() != null) order.setReceiverName(dto.getReceiverName());
        if (dto.getReceiverPhone() != null) order.setReceiverPhone(dto.getReceiverPhone());
        if (dto.getAddress() != null) order.setAddress(dto.getAddress());
        if (dto.getNote() != null) order.setNote(dto.getNote());

        // 3. 更新订单数量和金额（假设只有单商品订单）
        if (dto.getQuantity() != null && dto.getQuantity() > 0) {
            List<OrderItem> items = orderItemMapper.getOrderItemsByOrderId(String.valueOf(order.getId()));
            if (items != null && !items.isEmpty()) {
                OrderItem item = items.get(0); // 假设单商品订单

                item.setProductQuantity(dto.getQuantity());
                item.setProductTotal(item.getProductPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
                item.setUpdateTime(LocalDateTime.now());
                orderItemMapper.update(item); // 调用自定义 XML 中的 update 方法

                // 更新订单总金额
                order.setTotalAmount(item.getProductTotal());
            }
        }

        // 更新时间并保存订单
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order); // 调用自定义 XML 中的 update 方法
    }

    // 生成订单编号
    private String generateOrderSn() {
        // 使用UUID生成唯一订单编号
        return "ORDER" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
