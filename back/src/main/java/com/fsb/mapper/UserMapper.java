package com.fsb.Mapper;


import com.fsb.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select * from user where username= #{username}")
    User getByUsername(String username);

    @Select("select * from user where email = #{email}")
    User getByEmail(String email);

    @Insert("insert into user(username, password, email, nickname, phone, role, status, is_registered, create_time) " +
            "values(#{username}, #{password}, #{email}, #{nickname}, #{phone}, #{role}, #{status}, #{isRegistered}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Update("update user set password=#{password}, update_time=now() where id=#{id}")
    void updatePassword(@Param("id") Long id, @Param("password") String password);
}

