package com.fsb.Mapper;

import com.fsb.pojo.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminMapper {

    /**
     * 根据用户名查询管理员
     * @param username
     * @return
     */
    @Select("select * from admin where username=#{username}")
    Admin getByUsername(String username);

    @Update("update admin set password=#{password}, update_time=now() where id=#{id}")
    void updatePassword(@Param("id") Long id, @Param("password") String password);
}
