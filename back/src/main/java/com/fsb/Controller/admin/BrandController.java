package com.fsb.Controller.admin;

import com.fsb.Service.BrandService;
import com.fsb.pojo.DTO.BrandDTO;
import com.fsb.pojo.DTO.BrandPageQueryDTO;
import com.fsb.pojo.DTO.ProductPageQueryDTO;
import com.fsb.pojo.VO.BrandVO;
import com.fsb.pojo.entity.Brand;
import com.fsb.result.PageResult;
import com.fsb.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;


    /**
     * 查询所有品牌名称
     * @return
     */
    @GetMapping("/findBrandNames")
    public Result<List<String>> findBrandNames(){
        List<String> brandNames = brandService.findBrandNames();
        return Result.success(brandNames);
    }


    /**
     * 分页查询
     * @param brandPageQueryDTO
     * @return
     */
    @GetMapping("/listAll")
    public Result<PageResult> list(BrandPageQueryDTO brandPageQueryDTO)
    {
        PageResult pageResult = brandService.pageQuery(brandPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 添加品牌
     * @param brandDTO
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody BrandDTO brandDTO){
        log.info("添加品牌:{}", brandDTO);
        brandService.add(brandDTO);
        return Result.success();
    }

    /**
     * 修改品牌
     * @param brandDTO
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody BrandDTO brandDTO){
        log.info("修改品牌:{}", brandDTO);
        brandService.update(brandDTO);
        return Result.success();
    }

    @GetMapping("/getById/{id}")
    public Result<BrandVO> getById(@PathVariable Long id){
        log.info("查询id为{}的品牌信息", id);
        BrandVO brandVO = new BrandVO();
        Brand brand = brandService.getById(id);
        BeanUtils.copyProperties(brand, brandVO);
        return Result.success(brandVO);
    }

    /**
     * 删除品牌
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id){
        log.info("删除品牌:{}", id);
        brandService.delete(id);
        return Result.success();
    }

}
