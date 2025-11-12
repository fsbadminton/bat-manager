package com.fsb.mapper;

import com.fsb.entity.RacketSupplier;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RacketSupplierMapper {
    @Select("select * from RacketSupplier")
    List<RacketSupplier> findAll();

    @Insert("insert into RacketSupplier values(#{racketID},#{supplierID})")
    void add(RacketSupplier racketSupplier);

    @Update("update RacketSupplier set racketID=#{racketID},supplierID=#{supplierID} where racketID=#{racketID} and supplierID=#{supplierID}")
    void update(RacketSupplier racketSupplier);

    @Delete("delete from RacketSupplier where racketID= #{id}")
    void delete(int id);
}
