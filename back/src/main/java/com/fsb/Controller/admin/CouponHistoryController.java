package com.fsb.Controller.admin;

import com.fsb.Service.CouponService;
import com.fsb.pojo.DTO.CouponHistoryPageQueryDTO;
import com.fsb.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/couponHistory")
public class CouponHistoryController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(CouponHistoryPageQueryDTO dto) {
        return Result.success(couponService.pageHistory(dto));
    }
}
