package com.fsb.service.impl;

import com.fsb.entity.Supplier;
import com.fsb.mapper.SupplierMapper;
import com.fsb.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {
    @Autowired
    private SupplierMapper supplierMapper;

    @Override
    public List<Supplier> findAll() {
        return supplierMapper.findAll();
    }

    @Override
    public boolean add(Supplier supplier) {
        return supplierMapper.add(supplier)>0;
    }

    @Override
    public boolean update(Supplier supplier) {
        return supplierMapper.update(supplier)>0;
    }

    @Override
    public boolean delete(int id) {
        return supplierMapper.delete(id)>0;
    }
}
