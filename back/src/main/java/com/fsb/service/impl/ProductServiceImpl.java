package com.fsb.Service.impl;



import com.fsb.Mapper.BrandMapper;
import com.fsb.Mapper.CategoryMapper;
import com.fsb.Mapper.ProductMapper;
import com.fsb.Service.ProductService;
import com.fsb.pojo.DTO.ProductDTO;
import com.fsb.pojo.DTO.ProductPageQueryDTO;
import com.fsb.pojo.entity.Brand;
import com.fsb.pojo.entity.Category;
import com.fsb.pojo.entity.Product;
import com.fsb.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {


    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public PageResult pageQuery(ProductPageQueryDTO productPageQueryDTO) {
        PageHelper.startPage(productPageQueryDTO.getPageNum(), productPageQueryDTO.getPageSize());
        //product表里没有brandId--->先获取dto的brandId--->根据brandId查出品牌名---->回带
        //获取品牌id
        Long brandId = productPageQueryDTO.getBrandId();
        Integer category = productPageQueryDTO.getCategory();
        // 如果brandId不为null，则获取品牌名称
        if (brandId != null) {
            Brand brand = brandMapper.getById(brandId);
            if (brand != null) {
                productPageQueryDTO.setBrandName(brand.getName());
            }
        }
        Page<Product> page=productMapper.pageQuery(productPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(ProductDTO productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO,product);
        product.setIsDeleted(0);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.add(product);
    }

    @Override
    public void delete(Long id) {
        productMapper.delete(id);
    }

    @Override
    public void update(ProductDTO productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO,product);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.update(product);
    }

    @Override
    public Product getById(Long id) {
        return productMapper.getById(id);
    }

    @Override
    public void updateStatus(ProductDTO productDTO, Long id) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO,product);
        product.setProductId(id);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.update(product);
    }

    @Override
    public List<Product> findAll() {
        List<Product> productList = productMapper.findAll();
        return productList;
    }

    @Override
    public PageResult pageQueryByUser(ProductPageQueryDTO productPageQueryDTO) {
        PageHelper.startPage(productPageQueryDTO.getPageNum(), productPageQueryDTO.getPageSize());
        //product表里没有brandId--->先获取dto的brandId--->根据brandId查出品牌名---->回带
        //获取品牌id
        Long brandId = productPageQueryDTO.getBrandId();

        // 如果brandId不为null，则获取品牌名称
        if (brandId != null) {
            Brand brand = brandMapper.getById(brandId);
            if (brand != null) {
                productPageQueryDTO.setBrandName(brand.getName());
            }
        }
        Page<Product> page=productMapper.pageQueryByUser(productPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

}
