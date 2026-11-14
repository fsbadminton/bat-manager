package com.fsb.Controller.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsb.pojo.VO.AiCouponPageVO;
import com.fsb.pojo.entity.CouponHistory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiCouponVOTest {
    @Test
    void couponViewExcludesMemberAndRawRuleFields() throws Exception {
        CouponHistory coupon = new CouponHistory();
        coupon.setId(3L);
        coupon.setCouponName("Welcome");
        coupon.setAmount(new BigDecimal("50.00"));
        coupon.setMinPoint(new BigDecimal("0"));
        coupon.setUseStatus(0);
        coupon.setMemberUsername("private-user");
        coupon.setMemberNickname("private-name");
        coupon.setCouponCode("PRIVATE-CODE");
        coupon.setProductRelationJson("private-rule-json");
        coupon.setOrderSn("ORDER-3");
        String json = new ObjectMapper().findAndRegisterModules()
                .writeValueAsString(AiCouponPageVO.from(Map.of("total", 1, "list", List.of(coupon))));

        assertTrue(json.contains("Welcome"));
        assertTrue(json.contains("ORDER-3"));
        assertFalse(json.contains("private-user"));
        assertFalse(json.contains("private-name"));
        assertFalse(json.contains("PRIVATE-CODE"));
        assertFalse(json.contains("private-rule-json"));
    }
}
