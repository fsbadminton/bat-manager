package com.fsb.Service;

import com.fsb.entity.RacketSupplier;

import java.util.List;

public interface RacketSupplierService {
    //  查询所有供应表
    public List<RacketSupplier> findAll();
    //  添加供应表
    public void add(RacketSupplier racketSupplier);
    //  更新供应表
    public void update(RacketSupplier racketSupplier);
    //  删除供应表
    public void delete(int id);
}
