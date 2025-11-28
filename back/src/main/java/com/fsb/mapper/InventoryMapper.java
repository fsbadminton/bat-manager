package com.fsb.Mapper;

import com.fsb.pojo.entity.Inventory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InventoryMapper {
    //查询所有库存
    @Select("select * from inventory where isDeleted=0")
    List<Inventory> findAll();

    //添加库存
    @Options(useGeneratedKeys = true,keyProperty = "inventoryID",keyColumn = "InventoryID")
    @Insert("insert into inventory values(#{inventoryID},#{racketID},#{stock},#{warehouseLocation},0)")
    int add(Inventory inventory);

    //修改库存
    @Update("update inventory set stock=#{stock},warehouseLocation=#{warehouseLocation} where racketID=#{racketID} and isDeleted=0")
    int update(Inventory inventory);

    //软删除
    @Update("update inventory set isDeleted=1 where racketID= #{racketID}")
    int delete(int id);

    //  根据ID查询库存信息
    @Select("SELECT * FROM inventory WHERE racketID = #{racketID} and isDeleted=0")
    Inventory findByRacketId(int racketID);
    //  减库存
    @Update("UPDATE inventory SET stock = stock - #{quantity} WHERE racketID = #{racketID} and isDeleted=0")
    void reduceStock(@Param("racketID") int racketID, @Param("quantity") int quantity);
}
