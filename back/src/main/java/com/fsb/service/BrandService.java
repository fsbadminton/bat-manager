package com.fsb.service;

import com.fsb.entity.Brand;

import java.util.List;

public interface BrandService {
    //  查询所有品牌
    List<Brand> findAll();
    //  添加品牌信息
    boolean add(Brand brand);
    //  修改品牌信息
    boolean update(Brand brand);
    //  删除品牌信息
    boolean delete(int id);

    boolean exists(int brandID);
}
