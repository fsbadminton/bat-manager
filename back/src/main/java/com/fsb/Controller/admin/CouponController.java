package com.fsb.Controller.admin;

import com.fsb.Service.CouponService;
import com.fsb.pojo.DTO.CouponPageQueryDTO;
import com.fsb.pojo.entity.Coupon;
import com.fsb.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(CouponPageQueryDTO dto) {
        return Result.success(couponService.pageQuery(dto));
    }

    @PostMapping("/create")
    public Result<Void> create(@RequestBody Coupon coupon) {
        couponService.create(coupon);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Coupon> getById(@PathVariable Long id) {
        return Result.success(couponService.getById(id));
    }

    @PostMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Coupon coupon) {
        couponService.update(id, coupon);
        return Result.success();
    }

    @PostMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        couponService.delete(id);
        return Result.success();
    }
}
