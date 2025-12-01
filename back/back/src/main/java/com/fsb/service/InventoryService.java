package com.fsb.Service;

import com.fsb.entity.Inventory;

import java.util.List;

public interface InventoryService {
    //查询所有库存
    public List<Inventory> findAll();
    //添加库存
    public boolean add(Inventory inventory);
    //修改库存
    public boolean update(Inventory inventory);
    //删除库存
    public boolean delete(int id);
}
