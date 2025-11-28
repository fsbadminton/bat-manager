package com.fsb.Controller.admin;

import com.fsb.Service.OmsReturnApplyService;
import com.fsb.pojo.DTO.OmsReturnApplyDTO;
import com.fsb.pojo.DTO.OmsReturnApplyPageQueryDTO;
import com.fsb.pojo.VO.OmsReturnApplyVO;
import com.fsb.result.PageResult;
import com.fsb.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/returnApply")
public class OmsReturnApplyController {

    @Autowired
    private OmsReturnApplyService omsReturnApplyService;

    /**
     * 分页查询
     * @param omsReturnApplyPageQueryDTO
     * @return
     */
    @GetMapping("/list")
    public Result<PageResult> list(OmsReturnApplyPageQueryDTO omsReturnApplyPageQueryDTO){
        PageResult pageResult = omsReturnApplyService.pageQuery(omsReturnApplyPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 更新退货申请状态
     *
     */
    @PostMapping("/update/status/{id}")
    public Result updateStatus(@PathVariable Long id, @RequestBody OmsReturnApplyDTO dto) {
        omsReturnApplyService.updateStatus(id, dto);
        return Result.success();
    }


    /**
     * 查看退货申请详情
     * GET /api/returnApply/{id}
     */
    @GetMapping("/{id}")
    public Result<OmsReturnApplyVO> getReturnApplyDetail(@PathVariable Long id) {
        OmsReturnApplyVO detail = omsReturnApplyService.getDetail(id);
        return Result.success(detail);
    }
}
