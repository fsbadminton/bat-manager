package com.fsb.Mapper;

import com.fsb.pojo.DTO.CouponHistoryPageQueryDTO;
import com.fsb.pojo.DTO.UserCouponPageQueryDTO;
import com.fsb.pojo.entity.CouponHistory;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface CouponHistoryMapper {

    Page<CouponHistory> pageQueryAdmin(CouponHistoryPageQueryDTO dto);

    void insert(CouponHistory couponHistory);

    Page<CouponHistory> pageUserCoupons(@Param("username") String username, @Param("dto") UserCouponPageQueryDTO dto);

    CouponHistory getUserCouponById(@Param("id") Long id, @Param("username") String username);

    CouponHistory getJoinedById(Long id);

    @Select("select count(*) from coupon_history where coupon_id = #{couponId} and member_username = #{username}")
    Integer countByCouponIdAndUsername(@Param("couponId") Long couponId, @Param("username") String username);

    @Update("update coupon_history set use_status = 1, use_time = #{useTime}, order_id = #{orderId}, order_sn = #{orderSn} where id = #{id}")
    void markUsed(@Param("id") Long id, @Param("orderId") Long orderId, @Param("orderSn") String orderSn, @Param("useTime") LocalDateTime useTime);
}
