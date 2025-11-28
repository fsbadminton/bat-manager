package com.fsb.Mapper;

import com.fsb.pojo.DTO.CategoryPageQueryDTO;
import com.fsb.pojo.VO.CategoryVO;
import com.fsb.pojo.entity.Category;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {


    @Select("SELECT c.id, c.name, COUNT(p.product_id) as number FROM category c LEFT JOIN product p ON c.id = p.category GROUP BY c.id, c.name ORDER BY c.id")
    List<CategoryVO> getCategoryStatistics();


    @Insert("insert into category (name,create_time) values (#{name},#{createTime})")
    void add(Category category);


    void update(Category category);


    @Delete("delete from category where id = #{id}")
    void delete(Long id);


    @Select("SELECT c.id, c.name, COUNT(p.product_id) as number FROM category c LEFT JOIN product p ON c.id = p.category GROUP BY c.id, c.name ORDER BY c.id")
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);


    @Select("select * from category where id = #{id}")
    Category getById(Long id);


    @Select("select id from category")
    List<Integer> productCategory();


    @Select("select id,name from category")
    List<Category> selectList();
}
