package com.fsb.Controller.admin;


import com.fsb.Service.ProductService;
import com.fsb.pojo.DTO.BrandDTO;
import com.fsb.pojo.DTO.ProductDTO;
import com.fsb.pojo.DTO.ProductPageQueryDTO;
import com.fsb.pojo.entity.Brand;
import com.fsb.pojo.entity.Product;
import com.fsb.result.PageResult;
import com.fsb.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    /**
     * 分页查询
     * @param productPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> list(ProductPageQueryDTO productPageQueryDTO)
    {
        PageResult pageResult = productService.pageQuery(productPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 添加球拍
     * @param productDTO
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody ProductDTO productDTO){
        log.info("添加球拍:{}", productDTO);
        productService.add(productDTO);
        return Result.success();
    }

    /**
     * 删除品牌
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id){
        log.info("删除品牌:{}", id);
        productService.delete(id);
        return Result.success();
    }

    /**
     * 修改品牌
     * @param productDTO
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody ProductDTO productDTO){
        log.info("修改品牌信息:{}", productDTO);
        productService.update(productDTO);
        return Result.success();
    }

    /**
     * 根据id查询  用于修改球拍信息时进行数据回显
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    public Result<Product> getById(@PathVariable Long id){
        log.info("查询id为{}的球拍", id);
        Product product = productService.getById(id);
        return Result.success(product);
    }


    /**
     * 修改球拍状态  上架、新品、推荐
     * @param productDTO
     * @param id
     * @return
     */
    @PostMapping("/status/{id}")
    public Result updateStatus(@RequestBody ProductDTO productDTO,@PathVariable Long id){
        log.info("修改球拍状态:{} ,球拍id:{}", productDTO,id);
        productService.updateStatus(productDTO, id);
        return Result.success();

    }



}
