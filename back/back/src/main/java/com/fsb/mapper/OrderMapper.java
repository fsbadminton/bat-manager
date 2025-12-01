package com.fsb.Mapper;

import com.fsb.entity.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    //查询所有订单
    @Select("select orders.*,customer.Name from Orders join customer where orders.CustomerID=customer.CustomerID and orders.isDeleted=0 order by orders.OrderID asc ")
    List<Map<String,Object>> findAll();

    //添加订单
    @Insert("INSERT INTO Orders (CustomerID, OrderDate, Total, isDeleted) VALUES (#{customerID}, #{orderDate}, #{total}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "orderID")
    void add(Order order);

    @Update("update Orders set customerID= #{customerID},orderDate= #{orderDate},total= #{total} where orderID= #{orderID} and isDeleted=0")
    void update(Order order);

    //软删除订单
    @Update("update Orders set isDeleted=1 where orderID= #{orderID}")
    int delete(int id);

    //查询指定ID的订单
    @Select("SELECT orders.*,customer.Name FROM orders JOIN customer ON orders.CustomerID=customer.CustomerID WHERE customer.CustomerID=#{id} and orders.isDeleted=0")
    List<Order> findById(int id);

    //获取订单最大ID
    @Select("SELECT MAX(OrderID) FROM orders WHERE isDeleted=0")
    Integer findMaxId();
}
