package com.fsb.Service;


import com.fsb.pojo.DTO.CategoryDTO;
import com.fsb.pojo.DTO.CategoryPageQueryDTO;
import com.fsb.pojo.VO.CategoryVO;
import com.fsb.result.PageResult;

import java.util.List;

public interface CategoryService {
    List<CategoryVO> getCategoryStatistics();

    void add(CategoryDTO categoryDTO);

    void update(CategoryDTO categoryDTO);

    void delete(Long id);

    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    CategoryVO getById(Long id);

    List<Integer> productCategory();

    List<CategoryVO> listCategory();
}
