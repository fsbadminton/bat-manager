package com.fsb.Mapper;


import com.fsb.pojo.DTO.ReturnReasonPageQueryDTO;
import com.fsb.pojo.entity.ReturnReason;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReturnReasonMapper {

    @Select("SELECT * FROM return_reason")
    Page<ReturnReason> pageQuery(ReturnReasonPageQueryDTO returnReasonPageQueryDTO);


    @Insert("INSERT INTO return_reason (name, create_time) VALUES (#{name}, #{createTime})")
    void add(ReturnReason returnReason);


    @Select("SELECT * FROM return_reason WHERE id = #{id}")
    ReturnReason getById(Long id);

    @Update("UPDATE return_reason SET name = #{name} WHERE id = #{id}")
    void update(ReturnReason returnReason);



    void deleteBatch(List<Long> ids);


    @Select("SELECT * FROM return_reason")
    List<ReturnReason> listReason();
}
