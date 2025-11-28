package com.fsb.Controller.admin;


import com.fsb.Service.ReturnReasonService;
import com.fsb.pojo.DTO.ReturnReasonDTO;
import com.fsb.pojo.DTO.ReturnReasonPageQueryDTO;
import com.fsb.pojo.VO.ReturnReasonVO;
import com.fsb.result.PageResult;
import com.fsb.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/returnReason")
public class ReturnReasonController {


    @Autowired
    private ReturnReasonService returnReasonService;

    /**
     * 分页查询
     * @param returnReasonPageQueryDTO
     * @return
     */
    @GetMapping("/list")
    public Result<PageResult> pageQuery(ReturnReasonPageQueryDTO returnReasonPageQueryDTO){
        PageResult pageResult =returnReasonService.pageQuery(returnReasonPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 添加退货原因
     * @param returnReasonDTO
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody ReturnReasonDTO returnReasonDTO){
        returnReasonService.add(returnReasonDTO);
        return Result.success();
    }


    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    public Result<ReturnReasonVO> getById(@PathVariable Long id){
        ReturnReasonVO returnReasonVO = returnReasonService.getById(id);
        return Result.success(returnReasonVO);
    }

    @PostMapping("/update/{id}")
    public Result update(@PathVariable  Long id, @RequestBody ReturnReasonDTO returnReasonDTO){
        returnReasonDTO.setId(id);
        returnReasonService.update(returnReasonDTO);
        return Result.success();
    }


    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping("/delete")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        returnReasonService.deleteBatch(ids);
        return Result.success();
    }

}
