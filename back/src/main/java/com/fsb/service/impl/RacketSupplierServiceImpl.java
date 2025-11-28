package com.fsb.Service.impl;

import com.fsb.pojo.entity.RacketSupplier;
import com.fsb.Mapper.RacketSupplierMapper;
import com.fsb.Service.RacketSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RacketSupplierServiceImpl implements RacketSupplierService {

    @Autowired
    private RacketSupplierMapper racketSupplierMapper;
    @Override
    public List<RacketSupplier> findAll() {
        return racketSupplierMapper.findAll();
    }

    @Override
    public void add(RacketSupplier racketSupplier) {
        racketSupplierMapper.add(racketSupplier);
    }

    @Override
    public void update(RacketSupplier racketSupplier) {
        racketSupplierMapper.update(racketSupplier);
    }

    @Override
    public void delete(int id) {
        racketSupplierMapper.delete(id);
    }
}
