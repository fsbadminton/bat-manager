package com.fsb.Service;

import com.fsb.pojo.DTO.BrandDTO;
import com.fsb.pojo.DTO.ProductDTO;
import com.fsb.pojo.DTO.ProductPageQueryDTO;
import com.fsb.pojo.entity.Brand;
import com.fsb.pojo.entity.Product;
import com.fsb.result.PageResult;

import java.util.List;

public interface ProductService {
    PageResult pageQuery(ProductPageQueryDTO productPageQueryDTO);

    void add(ProductDTO productDTO);

    void delete(Long id);

    void update(ProductDTO productDTO);

    Product getById(Long id);

    void updateStatus(ProductDTO productDTO, Long id);

    List<Product> findAll();

    PageResult pageQueryByUser(ProductPageQueryDTO productPageQueryDTO);
}
