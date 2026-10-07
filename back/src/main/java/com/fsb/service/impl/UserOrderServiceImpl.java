package com.fsb.Service.impl;

import com.fsb.Mapper.OrderItemMapper;
import com.fsb.Mapper.OrderMapper;
import com.fsb.Mapper.ProductMapper;
import com.fsb.Mapper.ReturnApplyMapper;
import com.fsb.Service.CouponService;
import com.fsb.Service.UserOrderService;
import com.fsb.pojo.DTO.OrderPageQueryDTO;
import com.fsb.pojo.DTO.UserOrderCreateDTO;
import com.fsb.pojo.DTO.UserOrderUpdateDTO;
import com.fsb.pojo.entity.CouponHistory;
import com.fsb.pojo.VO.OrderVO;
import com.fsb.pojo.entity.OmsReturnApply;
import com.fsb.pojo.entity.Order;
import com.fsb.pojo.entity.OrderItem;
import com.fsb.pojo.entity.Product;
import com.fsb.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private ReturnApplyMapper returnApplyMapper;

    @Autowired
    private CouponService couponService;

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

        BigDecimal productPrice = product.getPrice() == null ? BigDecimal.ZERO : product.getPrice();
        BigDecimal originalAmount = productPrice.multiply(BigDecimal.valueOf(dto.getQuantity()));
        BigDecimal couponAmount = BigDecimal.ZERO;
        if (dto.getCouponHistoryId() != null) {
            CouponHistory couponHistory = couponService.validateCouponForOrder(dto.getCouponHistoryId(), username, dto.getProductId(), originalAmount);
            couponAmount = couponHistory.getAmount() == null ? BigDecimal.ZERO : couponHistory.getAmount();
            if (couponAmount.compareTo(originalAmount) > 0) {
                couponAmount = originalAmount;
            }
        }
        BigDecimal totalAmount = originalAmount.subtract(couponAmount);

        Order order = new Order();
        order.setMemberUsername(username);
        order.setTotalAmount(totalAmount);
        order.setCouponAmount(couponAmount);
        order.setCouponHistoryId(dto.getCouponHistoryId());
        order.setPayType(1);
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setAddress(dto.getAddress());
        order.setNote(dto.getNote());
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
        item.setProductPrice(productPrice);
        item.setProductQuantity(dto.getQuantity());
        item.setProductTotal(originalAmount);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        orderItemMapper.insert(item);

        if (dto.getCouponHistoryId() != null) {
            couponService.markCouponUsed(dto.getCouponHistoryId(), order.getId(), order.getOrderSn());
        }
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
            attachReturnInfo(order);
        }

        return new PageResult(page.getTotal(), orders);
    }

    public OrderVO getUserOrderDetail(Long orderId, String username) {
        Order order = getOwnedOrder(orderId, username);
        List<OrderItem> orderItems = orderItemMapper.getOrderItemsByOrderId(String.valueOf(orderId));
        order.setOrderItems(orderItems);
        attachReturnInfo(order);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderItems(orderItems);
        return orderVO;
    }

    @Override
    @Transactional
    public void updateUserOrder(UserOrderUpdateDTO dto, String username) {
        if (dto == null || dto.getId() == null) {
            throw new RuntimeException("订单不存在");
        }
        if (dto.getQuantity() == null || dto.getQuantity() < 1) {
            throw new RuntimeException("购买数量必须大于 0");
        }

        Order order = getOwnedOrder(dto.getId(), username);
        if (order.getStatus() == null || order.getStatus() != 1) {
            throw new RuntimeException("当前订单状态不支持修改");
        }

        OmsReturnApply latestApply = returnApplyMapper.getLatestByOrderId(order.getId());
        if (latestApply != null && latestApply.getStatus() != null && latestApply.getStatus() != 3) {
            throw new RuntimeException("订单正在售后中，暂不支持修改");
        }

        List<OrderItem> orderItems = orderItemMapper.getOrderItemsByOrderId(String.valueOf(order.getId()));
        if (orderItems == null || orderItems.isEmpty()) {
            throw new RuntimeException("订单商品不存在");
        }

        OrderItem orderItem = orderItems.get(0);
        Integer oldQuantity = orderItem.getProductQuantity() == null ? 0 : orderItem.getProductQuantity();
        int delta = dto.getQuantity() - oldQuantity;
        Product product = productMapper.selectById(orderItem.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (delta > 0) {
            if (product.getStock() == null || product.getStock() < delta) {
                throw new RuntimeException("商品库存不足");
            }
            productMapper.reduceStock(product.getProductId(), delta);
            productMapper.increaseSales(product.getProductId(), delta);
        } else if (delta < 0) {
            productMapper.increaseStock(product.getProductId(), -delta);
            productMapper.decreaseSales(product.getProductId(), -delta);
        }

        BigDecimal unitPrice = orderItem.getProductPrice() == null ? BigDecimal.ZERO : orderItem.getProductPrice();
        BigDecimal originalAmount = unitPrice.multiply(BigDecimal.valueOf(dto.getQuantity()));
        BigDecimal couponAmount = order.getCouponAmount() == null ? BigDecimal.ZERO : order.getCouponAmount();
        if (order.getCouponHistoryId() != null) {
            CouponHistory usedCoupon = couponService.getJoinedHistoryById(order.getCouponHistoryId());
            if (usedCoupon == null) {
                throw new RuntimeException("订单优惠券记录不存在");
            }
            BigDecimal minPoint = usedCoupon.getMinPoint() == null ? BigDecimal.ZERO : usedCoupon.getMinPoint();
            if (originalAmount.compareTo(minPoint) < 0) {
                throw new RuntimeException("修改后的订单金额已不满足已使用优惠券门槛");
            }
            couponAmount = usedCoupon.getAmount() == null ? BigDecimal.ZERO : usedCoupon.getAmount();
            if (couponAmount.compareTo(originalAmount) > 0) {
                couponAmount = originalAmount;
            }
        }
        BigDecimal totalAmount = originalAmount.subtract(couponAmount);

        orderItem.setProductQuantity(dto.getQuantity());
        orderItem.setProductTotal(originalAmount);
        orderItem.setUpdateTime(LocalDateTime.now());
        orderItemMapper.updateById(orderItem);

        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setAddress(dto.getAddress());
        order.setTotalAmount(totalAmount);
        order.setCouponAmount(couponAmount);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void confirmReceive(Long orderId, String username) {
        Order order = getOwnedOrder(orderId, username);
        if (order.getStatus() == null || order.getStatus() != 2) {
            throw new RuntimeException("当前订单还不能确认收货");
        }

        OmsReturnApply latestApply = returnApplyMapper.getLatestByOrderId(order.getId());
        if (latestApply != null && latestApply.getStatus() != null && (latestApply.getStatus() == 0 || latestApply.getStatus() == 1)) {
            throw new RuntimeException("售后处理中，暂不可确认收货");
        }

        order.setStatus(3);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    private String generateOrderSn() {
        return "ORDER" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    private Order getOwnedOrder(Long orderId, String username) {
        Order order = orderMapper.getById(orderId);
        if (order == null || order.getMemberUsername() == null || !order.getMemberUsername().equals(username)) {
            throw new RuntimeException("订单不存在");
        }
        return order;
    }

    private void attachReturnInfo(Order order) {
        if (order == null || order.getId() == null) {
            return;
        }
        OmsReturnApply latestApply = returnApplyMapper.getLatestByOrderId(order.getId());
        if (latestApply != null) {
            order.setReturnApplyStatus(latestApply.getStatus());
            order.setReturnApplyId(latestApply.getId());
        }
    }
}
