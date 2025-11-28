package com.fsb.Service;

import com.fsb.pojo.DTO.ReturnReasonDTO;
import com.fsb.pojo.DTO.ReturnReasonPageQueryDTO;
import com.fsb.pojo.VO.ReturnReasonVO;
import com.fsb.result.PageResult;

import java.util.List;

public interface ReturnReasonService {
    PageResult pageQuery(ReturnReasonPageQueryDTO returnReasonPageQueryDTO);

    void add(ReturnReasonDTO returnReasonDTO);

    ReturnReasonVO getById(Long id);

    void update(ReturnReasonDTO returnReasonDTO);

    void deleteBatch(List<Long> ids);

    List<ReturnReasonVO> listReason();
}
