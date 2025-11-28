package com.fsb.Mapper;

import com.fsb.pojo.DTO.BrandPageQueryDTO;
import com.fsb.pojo.DTO.ProductPageQueryDTO;
import com.fsb.pojo.entity.Brand;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BrandMapper {



    @Select("SELECT name FROM brand")
    List<String> findBrandNames();


    @Select("SELECT * FROM brand")
    List<Brand> list();

    Page<Brand> pageQuery(BrandPageQueryDTO brandPageQueryDTO);


    @Insert("insert into brand (name, create_time) VALUES (#{name},#{createTime})")
    void add(Brand brand);

    void update(Brand brand);


    @Delete("DELETE FROM brand WHERE id = #{id}")
    void delete(Long id);


    @Select("SELECT * FROM brand WHERE id = #{id}")
    Brand getById(Long id);
}
