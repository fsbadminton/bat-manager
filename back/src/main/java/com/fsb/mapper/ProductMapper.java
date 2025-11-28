package com.fsb.Mapper;

import com.fsb.pojo.DTO.ProductPageQueryDTO;
import com.fsb.pojo.entity.Brand;
import com.fsb.pojo.entity.Product;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper {


    Page<Product> pageQuery(ProductPageQueryDTO productPageQueryDTO);


    void add(Product product);


    @Update("update product set is_deleted = 1 where product_id= #{id}")
    void delete(Long id);


    @Select("SELECT * FROM product WHERE product_id = #{id} AND is_deleted = 0")
    Product getById(Long id);

    void update(Product product);


    @Select("SELECT * FROM product WHERE is_deleted = 0")
    List<Product> findAll();


    @Select("SELECT * FROM product WHERE product_id = #{productId} and is_deleted =0")
    Product selectById(Long productId);


    @Update("UPDATE product SET stock = stock - #{quantity} WHERE product_id = #{productId} AND stock >= #{quantity}")
    void reduceStock(Long productId, Integer quantity);


    @Update("UPDATE product SET sale = sale + #{quantity} WHERE product_id = #{productId}")
    void increaseSales(Long productId, Integer quantity);

    Page<Product> pageQueryByUser(ProductPageQueryDTO productPageQueryDTO);
}
