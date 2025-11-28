package com.fsb.Service;

import com.fsb.pojo.entity.Customer;

import java.util.List;

public interface CustomerService {
    // 查询所有客户
    public List<Customer> findAll();
    //  根据username和password查询客户
    public Customer findByUsernameAndPassword(String username, String password);

    //  根据username查询客户
    public List<Customer> findByUsername(String username);
    // 根据id查找客户
    public Customer findById(int id);

    //  添加客户
    public boolean add(Customer customer);
    //  更新客户信息
    public boolean update(Customer customer);
    //  删除客户
    public boolean delete(int id);
}
