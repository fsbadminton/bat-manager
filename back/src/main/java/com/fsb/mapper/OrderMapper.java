package com.fsb.Mapper;

import com.fsb.pojo.DTO.OrderPageQueryDTO;
import com.fsb.pojo.entity.Order;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {



    Page<Order> pageQuery(OrderPageQueryDTO orderPageQueryDTO);


    @Select("select * from `order` where id = #{id}")
    Order getById(Long id);

    void insert(Order order);


    @Select("select * from `order` where member_username = #{username}")
    Page<Order> listUserPageQuery(String username, OrderPageQueryDTO dto);


    void updateById(Order order);


    @Delete("delete from `order` where id = #{id}")
    void deleteById(Long id);
}
