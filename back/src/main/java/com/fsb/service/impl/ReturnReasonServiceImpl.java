package com.fsb.Service.impl;


import com.fsb.Mapper.ReturnReasonMapper;
import com.fsb.Service.ReturnReasonService;
import com.fsb.pojo.DTO.ReturnReasonDTO;
import com.fsb.pojo.DTO.ReturnReasonPageQueryDTO;
import com.fsb.pojo.VO.ReturnReasonVO;
import com.fsb.pojo.entity.ReturnReason;
import com.fsb.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReturnReasonServiceImpl implements ReturnReasonService {

    @Autowired
    private ReturnReasonMapper returnReasonMapper;

    @Override
    public PageResult pageQuery(ReturnReasonPageQueryDTO returnReasonPageQueryDTO) {
        PageHelper.startPage(returnReasonPageQueryDTO.getPageNum(), returnReasonPageQueryDTO.getPageSize());
        Page<ReturnReason> page=returnReasonMapper.pageQuery(returnReasonPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(ReturnReasonDTO returnReasonDTO) {
        ReturnReason returnReason = new ReturnReason();
        BeanUtils.copyProperties(returnReasonDTO,returnReason);
        returnReason.setCreateTime(LocalDateTime.now());
        returnReasonMapper.add(returnReason);
    }

    @Override
    public ReturnReasonVO getById(Long id) {
        ReturnReason returnReason = returnReasonMapper.getById(id);
        ReturnReasonVO returnReasonVO = new ReturnReasonVO();
        BeanUtils.copyProperties(returnReason,returnReasonVO);
        return returnReasonVO;
    }

    @Override
    public void update(ReturnReasonDTO returnReasonDTO) {
        ReturnReason returnReason = new ReturnReason();
        BeanUtils.copyProperties(returnReasonDTO,returnReason);
        returnReasonMapper.update(returnReason);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        returnReasonMapper.deleteBatch(ids);
    }

    @Override
    public List<ReturnReasonVO> listReason() {
        List<ReturnReason>  list=returnReasonMapper.listReason();
        List<ReturnReasonVO> listReasonVO=list.stream().map(item->{
            ReturnReasonVO returnReasonVO = new ReturnReasonVO();
            BeanUtils.copyProperties(item,returnReasonVO);
            return returnReasonVO;
        }).toList();
        return listReasonVO;
    }
}
