package com.fsb.mapper;

import com.fsb.entity.Supplier;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SupplierMapper {
    @Select("select * from supplier where isDeleted=0")
    List<Supplier> findAll();

    //添加供应商信息
    @Options(useGeneratedKeys = true,keyProperty = "supplierID",keyColumn = "supplierID")
    @Insert("insert into supplier values(#{supplierID},#{name},#{phone},#{supplyCategory},0)")
    int add(Supplier supplier);

    @Update("update supplier set name=#{name},phone=#{phone},supplyCategory=#{supplyCategory} where supplierID=#{supplierID} and isDeleted=0")
    int update(Supplier supplier);

    //软删除
    @Update("update supplier set isDeleted=1 where supplierID= #{supplierID}")
    int delete(int id);
}
