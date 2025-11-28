package com.fsb.Controller.admin;


import com.fsb.Service.BrandService;
import com.fsb.Service.CategoryService;
import com.fsb.Service.ProductService;
import com.fsb.pojo.DTO.CategoryDTO;
import com.fsb.pojo.DTO.CategoryPageQueryDTO;
import com.fsb.pojo.VO.CategoryVO;
import com.fsb.pojo.entity.Brand;
import com.fsb.pojo.entity.Product;
import com.fsb.result.PageResult;
import com.fsb.result.Result;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;



    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }



    /**
     * 新增分类
     */
    @PostMapping("/add")
    public Result add(@RequestBody CategoryDTO categoryDTO) {
        categoryService.add(categoryDTO);
        return Result.success();
    }

    /**
     * 修改分类
     */
    @PutMapping("/update/{id}")
    public Result update(@RequestBody CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return Result.success();
    }


    @GetMapping("/getById/{id}")
    public Result<CategoryVO> getById(@PathVariable Long id){
        CategoryVO categoryVO = categoryService.getById(id);
        return Result.success(categoryVO);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }



    /**
     * 获取球拍分类统计
     * @return
     */
//    @GetMapping("/list")
//    public Result<List<Integer>> productCategory(){
//        List<Integer> list = categoryService.productCategory();
//        return Result.success(list);
//    }


    @GetMapping("/list")
    public Result<List<CategoryVO>> listCategory() {
        List<CategoryVO> list = categoryService.listCategory();
        return Result.success(list);
    }


}
