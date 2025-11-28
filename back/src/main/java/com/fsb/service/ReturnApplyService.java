package com.fsb.Service;

import com.fsb.pojo.DTO.ReturnReasonDTO;
import com.fsb.pojo.VO.OmsReturnApplyVO;

import java.util.List;

public interface ReturnApplyService {
    void createReturnApply(ReturnReasonDTO dto);

    List<OmsReturnApplyVO> getUserReturnApplyList(String username);
}
