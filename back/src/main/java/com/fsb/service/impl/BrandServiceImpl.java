package com.fsb.Service.impl;


import com.fsb.pojo.DTO.BrandDTO;
import com.fsb.pojo.DTO.BrandPageQueryDTO;
import com.fsb.pojo.DTO.ProductPageQueryDTO;
import com.fsb.pojo.entity.Brand;
import com.fsb.Mapper.BrandMapper;
import com.fsb.Service.BrandService;
import com.fsb.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {


    @Autowired
    private BrandMapper brandMapper;


    @Override
    public List<String> findBrandNames() {
        List<String> brandNamesList = brandMapper.findBrandNames();
        return brandNamesList;
    }

    @Override
    public PageResult pageQuery(BrandPageQueryDTO brandPageQueryDTO) {
        int pageNum = brandPageQueryDTO.getPageNum() == null ? 1 : brandPageQueryDTO.getPageNum();
        int pageSize = brandPageQueryDTO.getPageSize() == null ? 10 : brandPageQueryDTO.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        Page<Brand> page = brandMapper.pageQuery(brandPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }


    /**
     * 添加品牌
     * @param brandDTO
     */
    @Override
    public void add(BrandDTO brandDTO) {
        Brand brand = new Brand();
        BeanUtils.copyProperties(brandDTO, brand);
        brand.setCreateTime(LocalDateTime.now());
        brandMapper.add(brand);
    }

    /**
     * 修改品牌
     * @param brandDTO
     */
    @Override
    public void update(BrandDTO brandDTO) {
        Brand brand = new Brand();
        BeanUtils.copyProperties(brandDTO, brand);
        brand.setUpdateTime(LocalDateTime.now());
        brandMapper.update(brand);
    }

    /**
     * 删除品牌
     * @param id
     */
    @Override
    public void delete(Long id) {
        brandMapper.delete(id);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public Brand getById(Long id) {
        return brandMapper.getById(id);
    }

}
