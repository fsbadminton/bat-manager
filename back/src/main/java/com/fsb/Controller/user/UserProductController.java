package com.fsb.Controller.user;


import com.fsb.Service.ProductService;
import com.fsb.Service.CategoryService;
import com.fsb.pojo.DTO.ProductPageQueryDTO;
import com.fsb.pojo.VO.CategoryVO;
import com.fsb.result.PageResult;
import com.fsb.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/product")
public class UserProductController {



    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 浏览球拍信息
     * @return
     */
    @GetMapping("/list")
    public Result<PageResult> list(ProductPageQueryDTO productPageQueryDTO){
        PageResult pageResult = productService.pageQueryByUser(productPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取用户端筛选所需的真实分类名称。
     */
    @GetMapping("/categories")
    public Result<List<CategoryVO>> categories() {
        return Result.success(categoryService.listCategory());
    }
}
