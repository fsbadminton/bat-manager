package com.fsb.Service.impl;


import com.fsb.entity.Brand;
import com.fsb.Mapper.BrandMapper;
import com.fsb.Service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;
    @Override
    public List<Brand> findAll() {
        return brandMapper.findAll();
    }

    @Override
    public boolean add(Brand brand) {
        return brandMapper.add(brand)>0;
    }

    @Override
    public boolean update(Brand brand) {
        return brandMapper.update(brand)>0;
    }

    @Override
    public boolean delete(int id) {
        return brandMapper.delete(id)>0;
    }

    @Override
    public boolean exists(int brandID) {
        return brandMapper.countBrandById(brandID) > 0;
    }
}
