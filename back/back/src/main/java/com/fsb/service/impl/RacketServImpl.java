package com.fsb.Service.impl;

import com.fsb.entity.Racket;
import com.fsb.Mapper.RacketMapper;
import com.fsb.Service.RacketService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RacketServImpl implements RacketService{
    @Autowired
    private RacketMapper  racketMapper;

    @Override
    public PageInfo<Racket> findByPage(int i, int size) {
        //设置分页参数
        PageHelper.startPage(i,size);
        //查询
        List<Racket> rackets = racketMapper.racketList();
        //PageInfo 会封装分页信息
        return new PageInfo<>(rackets);
    }

    @Override
    public List<Racket> racketList() {
        return racketMapper.racketList();
    }

    @Override
    public boolean add(Racket racket) {
        return racketMapper.add(racket)>0;
    }

    @Override
    public boolean update(Racket racket) {
        return racketMapper.update(racket)>0;
    }

    @Override
    public boolean delete(int id) {
        return racketMapper.delete(id)>0;
    }

    @Override
    public Racket findById(int id) {
        return racketMapper.findById(id);
    }

    @Override
    public boolean exists(int racketID) {
        return racketMapper.countRacketById(racketID)>0;
    }
}
