package com.fsb.Mapper;

import com.fsb.pojo.DTO.CouponPageQueryDTO;
import com.fsb.pojo.entity.Coupon;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CouponMapper {

    Page<Coupon> pageQuery(CouponPageQueryDTO dto);

    void insert(Coupon coupon);

    Coupon getById(Long id);

    @Select("select * from coupon where note = #{note} limit 1")
    Coupon getByNote(String note);

    void updateById(Coupon coupon);

    void deleteById(Long id);

    Page<Coupon> pageAvailableForUser(@Param("username") String username);

    @Select("select count(*) from coupon_history where coupon_id = #{couponId}")
    Integer countReceivedByCouponId(Long couponId);

    @Select("select count(*) from coupon_history where coupon_id = #{couponId} and use_status = 1")
    Integer countUsedByCouponId(Long couponId);
}
