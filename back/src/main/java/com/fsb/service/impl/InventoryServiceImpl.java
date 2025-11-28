package com.fsb.Service.impl;

import com.fsb.pojo.entity.Inventory;
import com.fsb.Mapper.InventoryMapper;
import com.fsb.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryMapper inventoryMapper;
    @Override
    public List<Inventory> findAll() {
        return inventoryMapper.findAll();
    }

    @Override
    public boolean add(Inventory inventory) {
        return  inventoryMapper.add(inventory)>0;
    }

    @Override
    public boolean update(Inventory inventory) {
        return inventoryMapper.update(inventory)>0;
    }

    @Override
    public boolean delete(int id) {
        return  inventoryMapper.delete(id)>0;
    }
}
