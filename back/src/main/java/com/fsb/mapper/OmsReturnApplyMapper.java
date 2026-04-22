package com.fsb.Mapper;


import com.fsb.pojo.DTO.OmsReturnApplyPageQueryDTO;
import com.fsb.pojo.entity.OmsReturnApply;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OmsReturnApplyMapper {
    Page<OmsReturnApply> pageQuery(OmsReturnApplyPageQueryDTO omsReturnApplyPageQueryDTO);


    @Select("select * from return_apply where id=#{id}")
    OmsReturnApply getById(Long id);

    @Select("select * from return_apply where order_id = #{orderId} order by id desc limit 1")
    OmsReturnApply getLatestByOrderId(Long orderId);

    void updateById(OmsReturnApply apply);
}
