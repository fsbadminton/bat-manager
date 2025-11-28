package com.fsb.mapper;

import com.fsb.entity.Brand;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BrandMapper {
    // 查询所有品牌信息
    @Select("select * from brand where isDeleted=0")
    List<Brand> findAll();
    // 添加品牌信息
    //useGeneratedKeys-->使用数据库自动生成主键
    //keyProperty-->将生成的主键赋值给实体类中的 brandid 字段
    //keyColumn-->对应数据库中的主键字段名
    @Options(useGeneratedKeys = true,keyProperty = "BrandID",keyColumn = "BrandID")
    @Insert("insert into brand (name, country, website, isDeleted) VALUES (#{Name},#{Country},#{Website},0)")
    int add(Brand brand);
    //  修改品牌信息
    @Update("UPDATE Brand SET Name= #{Name}, Country= #{Country}, Website= #{Website} WHERE BrandID= #{BrandID} AND isDeleted =0")
    int update(Brand brand);
    //  删除品牌信息(软删除)
    @Update("update brand set isDeleted = 1 where BrandID=#{BrandID}")
    int delete(int id);

    //  查询是否存在品牌
    @Select("SELECT COUNT(*) FROM brand WHERE brandID = #{BrandID} and isDeleted=0")
    int countBrandById(int brandID);
}
