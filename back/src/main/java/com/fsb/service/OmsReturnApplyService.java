package com.fsb.Service;

import com.fsb.pojo.DTO.OmsReturnApplyDTO;
import com.fsb.pojo.DTO.OmsReturnApplyPageQueryDTO;
import com.fsb.pojo.VO.OmsReturnApplyVO;
import com.fsb.result.PageResult;

public interface OmsReturnApplyService {
    PageResult pageQuery(OmsReturnApplyPageQueryDTO omsReturnApplyPageQueryDTO);

    void updateStatus(Long id, OmsReturnApplyDTO dto);

    OmsReturnApplyVO getDetail(Long id);
}
