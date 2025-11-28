package com.fsb.Controller.user;


import com.fsb.Service.BrandService;
import com.fsb.Service.UserBrandService;
import com.fsb.pojo.DTO.BrandPageQueryDTO;
import com.fsb.result.PageResult;
import com.fsb.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/brand")
public class UserBrandController {

    @Autowired
    private UserBrandService userBrandService;

    @Autowired
    private BrandService brandService;

    @GetMapping("/list")
    public Result<PageResult> list(BrandPageQueryDTO brandPageQueryDTO)
    {
        PageResult pageResult = brandService.pageQuery(brandPageQueryDTO);
        return Result.success(pageResult);
    }

}
