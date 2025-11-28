package com.fsb.Service;

import com.fsb.pojo.entity.Supplier;

import java.util.List;

public interface SupplierService {
    //查询所有供货商
    List<Supplier> findAll();
    //添加供货商
    boolean add(Supplier supplier);
    //修改供货商信息
    boolean update(Supplier supplier);
    //删除供货商
    boolean delete(int id);
}
