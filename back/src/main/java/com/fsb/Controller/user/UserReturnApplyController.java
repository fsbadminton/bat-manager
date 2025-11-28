package com.fsb.Controller.user;


import com.fsb.Service.ReturnApplyService;
import com.fsb.Service.ReturnReasonService;
import com.fsb.pojo.DTO.ReturnReasonDTO;
import com.fsb.pojo.VO.OmsReturnApplyVO;
import com.fsb.pojo.VO.ReturnReasonVO;
import com.fsb.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/returnApply")
public class UserReturnApplyController {
    @Autowired
    private ReturnApplyService returnApplyService;

    @Autowired
    private ReturnReasonService returnReasonService;


    @GetMapping("/list")
    public Result<List<ReturnReasonVO>> list() {
        //Long userId = (Long) request.getAttribute("userId");
        String username = "zhangsan";
        List<ReturnReasonVO> listReason=returnReasonService.listReason();
        return Result.success(listReason);
    }

    // 用户提交退货申请
    @PostMapping("/create")
    public Result create(@RequestBody ReturnReasonDTO dto) {
        //Long userId = (Long) request.getAttribute("userId"); // 从 token 获取
        dto.setName("zhangsan");
        returnApplyService.createReturnApply(dto);
        return Result.success("退货申请已提交");
    }

    // 用户查询自己的退货申请列表
    @GetMapping("/listAll")
    public Result list(String username) {
        //Long userId = (Long) request.getAttribute("userId");
        username = "zhangsan";
        List<OmsReturnApplyVO> list = returnApplyService.getUserReturnApplyList(username);
        return Result.success(list);
    }
}
