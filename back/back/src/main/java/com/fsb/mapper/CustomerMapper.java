package com.fsb.Mapper;

import com.fsb.entity.Customer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CustomerMapper {
    //查询所有用户
    @Select("select * from customer where isDeleted=0")
    List<Customer> findAll();

    //添加用户
    @Options(useGeneratedKeys = true,keyProperty = "customerID",keyColumn = "customerID")
    @Insert("insert into customer values(#{customerID},#{name},#{phone},#{address},#{RegisterDate},#{Password},#{isDeleted})")
    int add(Customer customer);

    //修改用户
    @Update("update customer set name=#{name},phone=#{phone},address=#{address} where customerID=#{customerID} and isDeleted=0")
    int update(Customer customer);

    //软删除用户
    @Update("update customer set isDeleted=1 where customerID= #{id}")
    int delete(int id);

    //根据用户名和密码查询用户
    @Select("SELECT * FROM customer WHERE Name=#{username} and Password=#{password} and isDeleted=0")
    Customer findByUsernameAndPassword(String username, String password);


    //根据id查询用户
    @Select("SELECT * FROM customer WHERE customerID=#{id} and isDeleted=0")
    Customer findById(int id);

    //模糊查询用户
    @Select("SELECT * FROM customer WHERE Name LIKE concat('%',#{username},'%') and isDeleted=0")
    List<Customer> findByUsername(String username);
}
