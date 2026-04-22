package com.fsb.Mapper;

import com.fsb.pojo.VO.OrderItemVO;
import com.fsb.pojo.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemMapper {


    // 查询订单明细
    @Select("SELECT * FROM order_item WHERE order_id = #{id}")
    List<OrderItem> getOrderItemsByOrderId(String id);


    @Select("SELECT order_sn FROM `order` WHERE id = #{id}")
    Long getOrderSnByOrderSn(Long id);

    void insert(OrderItem item);

    void updateById(OrderItem item);

    @Select("SELECT order_id FROM order_item WHERE id = #{id}")
    Long getOrderId(Long id);
}
