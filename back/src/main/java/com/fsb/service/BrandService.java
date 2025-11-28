package com.fsb.Service;

import com.fsb.pojo.DTO.BrandDTO;
import com.fsb.pojo.DTO.BrandPageQueryDTO;
import com.fsb.pojo.DTO.ProductPageQueryDTO;
import com.fsb.pojo.entity.Brand;
import com.fsb.result.PageResult;

import java.util.List;

public interface BrandService {


    List<String> findBrandNames();



    PageResult pageQuery(BrandPageQueryDTO brandPageQueryDTO);

    void add(BrandDTO brandDTO);

    void update(BrandDTO brandDTO);

    void delete(Long id);

    Brand getById(Long id);
}
