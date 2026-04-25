package com.fsb.Service;

import com.fsb.pojo.DTO.CouponHistoryPageQueryDTO;
import com.fsb.pojo.DTO.CouponPageQueryDTO;
import com.fsb.pojo.DTO.UserCouponPageQueryDTO;
import com.fsb.pojo.entity.Coupon;
import com.fsb.pojo.entity.CouponHistory;

import java.math.BigDecimal;
import java.util.Map;

public interface CouponService {

    Map<String, Object> pageQuery(CouponPageQueryDTO dto);

    void create(Coupon coupon);

    Coupon getById(Long id);

    void update(Long id, Coupon coupon);

    void delete(Long id);

    Map<String, Object> pageHistory(CouponHistoryPageQueryDTO dto);

    Map<String, Object> pageAvailableForUser(String username, UserCouponPageQueryDTO dto);

    Map<String, Object> pageUserCoupons(String username, UserCouponPageQueryDTO dto);

    void claimCoupon(Long couponId, String username);

    CouponHistory validateCouponForOrder(Long couponHistoryId, String username, Long productId, BigDecimal orderAmount);

    CouponHistory getJoinedHistoryById(Long id);

    void markCouponUsed(Long couponHistoryId, Long orderId, String orderSn);
}
