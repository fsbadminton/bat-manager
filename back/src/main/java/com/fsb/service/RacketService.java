package com.fsb.service;

import com.fsb.entity.Racket;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface RacketService {
    //分页查询所有球拍信息
//    public List<Racket> racketList();
    PageInfo<Racket> findByPage(int i, int size);
    //查询所有球拍信息
    List<Racket> racketList();
    //添加球拍信息
    boolean add(Racket racket);
    //修改球拍价格
    boolean update(Racket racket);
    //根据id删除球拍
    boolean delete(int id);

    //根据id查询球拍
    Racket findById(int id);

    boolean exists(int racketID);
}
