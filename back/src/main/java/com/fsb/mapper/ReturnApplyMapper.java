package com.fsb.Mapper;


import com.fsb.pojo.VO.OmsReturnApplyVO;
import com.fsb.pojo.entity.OmsReturnApply;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReturnApplyMapper {

    @Insert("INSERT INTO return_apply(order_id, user_id ,username, reason, status, create_time) " +
            "VALUES(#{orderId},#{userId} , #{username}, #{reason}, #{status}, #{createTime})")
    void insertReturnApply(OmsReturnApply apply);


    @Select("select * from return_apply where username =#{username}")
    List<OmsReturnApplyVO> listByUsername(String username);

    @Select("select * from return_apply where order_id = #{orderId} order by id desc limit 1")
    OmsReturnApply getLatestByOrderId(Long orderId);

    @Select("select count(*) from return_apply where order_id = #{orderId} and status in (0,1)")
    Integer countActiveByOrderId(Long orderId);
}
