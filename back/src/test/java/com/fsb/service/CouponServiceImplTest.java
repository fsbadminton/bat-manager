package com.fsb.service;

import com.fsb.Mapper.CouponHistoryMapper;
import com.fsb.Mapper.CouponMapper;
import com.fsb.Mapper.ProductMapper;
import com.fsb.Service.impl.CouponServiceImpl;
import com.fsb.pojo.entity.Coupon;
import com.fsb.pojo.entity.CouponHistory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponMapper couponMapper;

    @Mock
    private CouponHistoryMapper couponHistoryMapper;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private CouponServiceImpl service;

    @Test
    void claimCouponCreatesUnusedHistoryForCurrentUser() {
        Coupon coupon = new Coupon();
        coupon.setId(7L);
        coupon.setPublishCount(20);
        coupon.setPerLimit(2);
        coupon.setEnableTime(LocalDateTime.now().minusMinutes(1));
        coupon.setEndTime(LocalDateTime.now().plusDays(1));
        when(couponMapper.getById(7L)).thenReturn(coupon);
        when(couponMapper.countReceivedByCouponId(7L)).thenReturn(3);
        when(couponHistoryMapper.countByCouponIdAndUsername(7L, "zhangsan")).thenReturn(1);

        service.claimCoupon(7L, "zhangsan");

        ArgumentCaptor<CouponHistory> captor = ArgumentCaptor.forClass(CouponHistory.class);
        verify(couponHistoryMapper).insert(captor.capture());
        CouponHistory saved = captor.getValue();
        assertEquals(7L, saved.getCouponId());
        assertEquals("zhangsan", saved.getMemberUsername());
        assertEquals(0, saved.getUseStatus());
        assertTrue(saved.getCouponCode().startsWith("CP"));
    }

    @Test
    void claimCouponRejectsUserOverPerLimit() {
        Coupon coupon = new Coupon();
        coupon.setId(7L);
        coupon.setPerLimit(1);
        when(couponMapper.getById(7L)).thenReturn(coupon);
        when(couponMapper.countReceivedByCouponId(7L)).thenReturn(0);
        when(couponHistoryMapper.countByCouponIdAndUsername(7L, "zhangsan")).thenReturn(1);

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> service.claimCoupon(7L, "zhangsan"));

        assertEquals("已达到该优惠券领取上限", error.getMessage());
        verify(couponHistoryMapper, never()).insert(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void newcomerCouponCannotBeClaimedManually() {
        Coupon coupon = new Coupon();
        coupon.setId(8L);
        coupon.setNote("system_code:NEW_USER_50");
        when(couponMapper.getById(8L)).thenReturn(coupon);

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> service.claimCoupon(8L, "zhangsan"));

        assertEquals("新人优惠券仅在注册时自动发放", error.getMessage());
        verify(couponHistoryMapper, never()).insert(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void validateCouponRejectsCouponNotOwnedByUser() {
        when(couponHistoryMapper.getUserCouponById(9L, "zhangsan")).thenReturn(null);

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> service.validateCouponForOrder(9L, "zhangsan", 1L, new BigDecimal("100.00")));

        assertEquals("优惠券不存在", error.getMessage());
    }

    @Test
    void validateCouponRejectsExpiredCouponAndInsufficientOrderAmount() {
        CouponHistory expired = usableHistory();
        expired.setEndTime(LocalDateTime.now().minusSeconds(1));
        when(couponHistoryMapper.getUserCouponById(10L, "zhangsan")).thenReturn(expired);

        RuntimeException expiredError = assertThrows(RuntimeException.class,
                () -> service.validateCouponForOrder(10L, "zhangsan", 1L, new BigDecimal("100.00")));
        assertEquals("优惠券已过期", expiredError.getMessage());

        CouponHistory belowThreshold = usableHistory();
        belowThreshold.setMinPoint(new BigDecimal("200.00"));
        when(couponHistoryMapper.getUserCouponById(11L, "zhangsan")).thenReturn(belowThreshold);

        RuntimeException thresholdError = assertThrows(RuntimeException.class,
                () -> service.validateCouponForOrder(11L, "zhangsan", 1L, new BigDecimal("199.99")));
        assertEquals("当前订单金额未达到优惠券使用门槛", thresholdError.getMessage());
    }

    @Test
    void validateCouponAcceptsMatchingProductRestriction() {
        CouponHistory history = usableHistory();
        history.setUseType(2);
        history.setProductRelationJson("[{\"productId\":42}]");
        when(couponHistoryMapper.getUserCouponById(12L, "zhangsan")).thenReturn(history);

        CouponHistory result = service.validateCouponForOrder(
                12L, "zhangsan", 42L, new BigDecimal("100.00"));

        assertSame(history, result);
    }

    @Test
    void grantCouponToUserIsIdempotent() {
        Coupon coupon = new Coupon();
        coupon.setId(20L);
        when(couponMapper.getByNote("system_code:NEW_USER_50")).thenReturn(coupon);
        when(couponHistoryMapper.countByCouponIdAndUsername(20L, "new-user")).thenReturn(0);

        service.grantCouponToUser("system_code:NEW_USER_50", "new-user", "New User");

        ArgumentCaptor<CouponHistory> captor = ArgumentCaptor.forClass(CouponHistory.class);
        verify(couponHistoryMapper).insert(captor.capture());
        assertEquals(20L, captor.getValue().getCouponId());
        assertEquals("new-user", captor.getValue().getMemberUsername());
        assertEquals("New User", captor.getValue().getMemberNickname());
        assertEquals(2, captor.getValue().getGetType());
    }

    private CouponHistory usableHistory() {
        CouponHistory history = new CouponHistory();
        history.setUseStatus(0);
        history.setStartTime(LocalDateTime.now().minusDays(1));
        history.setEndTime(LocalDateTime.now().plusDays(1));
        history.setMinPoint(BigDecimal.ZERO);
        history.setUseType(0);
        return history;
    }
}
