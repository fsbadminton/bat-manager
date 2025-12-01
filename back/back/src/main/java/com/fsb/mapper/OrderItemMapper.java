package com.fsb.Mapper;

import com.fsb.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    @Select("select * from orderitem")
    List<OrderItem> findAll();

    //  添加订单信息
    @Insert("insert into orderitem (OrderID, RacketID, Quantity, Price) VALUES (#{orderID},#{racketID},#{quantity},#{price})")
    void add(OrderItem orderItem);

    @Update("update orderitem set OrderID=#{orderID},RacketID=#{racketID},Quantity=#{quantity},Price=#{price} where OrderID=#{orderID} and RacketID=#{racketID}")
    void update(OrderItem orderItem);

    @Delete("delete from orderitem where OrderID= #{id}")
    void delete(int id);

    @Select("select * from orderitem where OrderID= #{id}")
    OrderItem findById(int id);

    // 获取最大的OrderID
    @Select("SELECT MAX(OrderID) FROM orderitem")
    Integer findMaxId();
}
