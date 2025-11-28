package com.fsb.Service.impl;

import com.fsb.Mapper.CategoryMapper;
import com.fsb.Service.CategoryService;
import com.fsb.pojo.DTO.CategoryDTO;
import com.fsb.pojo.DTO.CategoryPageQueryDTO;
import com.fsb.pojo.VO.CategoryVO;
import com.fsb.pojo.entity.Category;
import com.fsb.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 获取球拍分类统计
     * @return
     */
    @Override
    public List<CategoryVO> getCategoryStatistics() {
        List<CategoryVO> categoryVOList = categoryMapper.getCategoryStatistics();
        return categoryVOList;
    }


    /**
     * 新增分类
     */
    @Override
    public void add(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setCreateTime(LocalDateTime.now());
        categoryMapper.add(category);
    }


    /**
     * 修改分类
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.update(category);
    }

    /**
     * 删除分类
     */
    @Override
    public void delete(Long id) {
        categoryMapper.delete(id);
    }

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPageNum(),categoryPageQueryDTO.getPageSize());
        Page<Category> page= categoryMapper.pageQuery(categoryPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public CategoryVO getById(Long id) {
        Category category = categoryMapper.getById(id);
        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category,categoryVO);
        return categoryVO;
    }

    @Override
    public List<Integer> productCategory() {
        List<Integer> list = categoryMapper.productCategory();
        return list;
    }

    @Override
    public List<CategoryVO> listCategory() {
        List<Category> categories = categoryMapper.selectList();

        return categories.stream().map(cat -> {
            CategoryVO vo = new CategoryVO();
            vo.setId(cat.getId());
            vo.setName(cat.getName());
            return vo;
        }).collect(Collectors.toList());
    }
}
