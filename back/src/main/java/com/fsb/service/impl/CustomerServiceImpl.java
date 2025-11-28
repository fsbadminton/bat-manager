package com.fsb.Service.impl;

import com.fsb.pojo.entity.Customer;
import com.fsb.Mapper.CustomerMapper;
import com.fsb.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerMapper customerMapper;
    @Override
    public List<Customer> findAll() {
        return customerMapper.findAll();
    }

    @Override
    public Customer findByUsernameAndPassword(String username, String password) {
        return customerMapper.findByUsernameAndPassword(username,password);
    }

    @Override
    public List<Customer> findByUsername(String username) {
        return customerMapper.findByUsername(username);
    }

    @Override
    public Customer findById(int id) {
        return customerMapper.findById(id);
    }

    @Override
    public boolean add(Customer customer) {
        return customerMapper.add(customer)>0;
    }

    @Override
    public boolean update(Customer customer) {
        return customerMapper.update(customer)>0;
    }

    @Override
    public boolean delete(int id) {
        return customerMapper.delete(id)>0;
    }
}
