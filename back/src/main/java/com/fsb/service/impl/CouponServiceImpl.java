package com.fsb.Service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsb.Mapper.CouponHistoryMapper;
import com.fsb.Mapper.CouponMapper;
import com.fsb.Mapper.ProductMapper;
import com.fsb.Service.CouponService;
import com.fsb.pojo.DTO.CouponHistoryPageQueryDTO;
import com.fsb.pojo.DTO.CouponPageQueryDTO;
import com.fsb.pojo.DTO.UserCouponPageQueryDTO;
import com.fsb.pojo.entity.Coupon;
import com.fsb.pojo.entity.CouponHistory;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CouponServiceImpl implements CouponService {

    private static final TypeReference<List<Map<String, Object>>> LIST_MAP_TYPE = new TypeReference<List<Map<String, Object>>>() {};

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private CouponHistoryMapper couponHistoryMapper;

    @Autowired
    private ProductMapper productMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> pageQuery(CouponPageQueryDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        Page<Coupon> page = couponMapper.pageQuery(dto);
        List<Coupon> list = page.getResult();
        for (Coupon coupon : list) {
            fillCouponJsonFields(coupon);
        }
        return pageResult(list, page.getTotal());
    }

    @Override
    @Transactional
    public void create(Coupon coupon) {
        normalizeCoupon(coupon);
        coupon.setCreateTime(LocalDateTime.now());
        coupon.setUpdateTime(LocalDateTime.now());
        couponMapper.insert(coupon);
    }

    @Override
    public Coupon getById(Long id) {
        Coupon coupon = couponMapper.getById(id);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }
        fillCouponJsonFields(coupon);
        return coupon;
    }

    @Override
    @Transactional
    public void update(Long id, Coupon coupon) {
        Coupon existing = couponMapper.getById(id);
        if (existing == null) {
            throw new RuntimeException("优惠券不存在");
        }
        coupon.setId(id);
        normalizeCoupon(coupon);
        coupon.setUpdateTime(LocalDateTime.now());
        couponMapper.updateById(coupon);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Coupon coupon = couponMapper.getById(id);
        if (coupon == null) {
            return;
        }
        Integer receiveCount = couponMapper.countReceivedByCouponId(id);
        if (receiveCount != null && receiveCount > 0) {
            throw new RuntimeException("该优惠券已被领取，不能直接删除");
        }
        couponMapper.deleteById(id);
    }

    @Override
    public Map<String, Object> pageHistory(CouponHistoryPageQueryDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        Page<CouponHistory> page = couponHistoryMapper.pageQueryAdmin(dto);
        List<CouponHistory> list = page.getResult();
        for (CouponHistory item : list) {
            fillCouponHistoryJsonFields(item);
        }
        return pageResult(list, page.getTotal());
    }

    @Override
    public Map<String, Object> pageAvailableForUser(String username, UserCouponPageQueryDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        Page<Coupon> page = couponMapper.pageAvailableForUser(username);
        List<Coupon> list = page.getResult();
        for (Coupon coupon : list) {
            fillCouponJsonFields(coupon);
        }
        return pageResult(list, page.getTotal());
    }

    @Override
    public Map<String, Object> pageUserCoupons(String username, UserCouponPageQueryDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        Page<CouponHistory> page = couponHistoryMapper.pageUserCoupons(username, dto);
        List<CouponHistory> list = page.getResult();
        for (CouponHistory item : list) {
            fillCouponHistoryJsonFields(item);
        }
        return pageResult(list, page.getTotal());
    }

    @Override
    @Transactional
    public void claimCoupon(Long couponId, String username) {
        Coupon coupon = couponMapper.getById(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }
        if ("system_code:NEW_USER_50".equals(coupon.getNote())) {
            throw new RuntimeException("新人优惠券仅在注册时自动发放");
        }
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getEnableTime() != null && now.isBefore(coupon.getEnableTime())) {
            throw new RuntimeException("该优惠券还未到领取时间");
        }
        if (coupon.getEndTime() != null && now.isAfter(coupon.getEndTime())) {
            throw new RuntimeException("该优惠券已过期");
        }
        Integer received = couponMapper.countReceivedByCouponId(couponId);
        if (coupon.getPublishCount() != null && received != null && received >= coupon.getPublishCount()) {
            throw new RuntimeException("优惠券已被抢完");
        }
        Integer userCount = couponHistoryMapper.countByCouponIdAndUsername(couponId, username);
        Integer perLimit = coupon.getPerLimit() == null ? 1 : coupon.getPerLimit();
        if (userCount != null && userCount >= perLimit) {
            throw new RuntimeException("已达到该优惠券领取上限");
        }

        CouponHistory history = new CouponHistory();
        history.setCouponId(couponId);
        history.setCouponCode(generateCouponCode());
        history.setMemberUsername(username);
        history.setMemberNickname(username);
        history.setGetType(1);
        history.setCreateTime(now);
        history.setUseStatus(0);
        couponHistoryMapper.insert(history);
    }

    @Override
    public CouponHistory validateCouponForOrder(Long couponHistoryId, String username, Long productId, BigDecimal orderAmount) {
        CouponHistory history = couponHistoryMapper.getUserCouponById(couponHistoryId, username);
        if (history == null) {
            throw new RuntimeException("优惠券不存在");
        }
        if (history.getUseStatus() == null || history.getUseStatus() != 0) {
            throw new RuntimeException("该优惠券已使用或不可用");
        }

        LocalDateTime now = LocalDateTime.now();
        if (history.getStartTime() != null && now.isBefore(history.getStartTime())) {
            throw new RuntimeException("优惠券未到生效时间");
        }
        if (history.getEndTime() != null && now.isAfter(history.getEndTime())) {
            throw new RuntimeException("优惠券已过期");
        }

        BigDecimal minPoint = history.getMinPoint() == null ? BigDecimal.ZERO : history.getMinPoint();
        if (orderAmount.compareTo(minPoint) < 0) {
            throw new RuntimeException("当前订单金额未达到优惠券使用门槛");
        }

        if (history.getUseType() != null && history.getUseType() == 2 && !containsProduct(history.getProductRelationJson(), productId)) {
            throw new RuntimeException("该优惠券不适用于当前商品");
        }
        if (history.getUseType() != null && history.getUseType() == 1 && !containsCategory(history.getProductCategoryRelationJson(), productId)) {
            throw new RuntimeException("该优惠券不适用于当前商品分类");
        }

        return history;
    }

    @Override
    public CouponHistory getJoinedHistoryById(Long id) {
        CouponHistory history = couponHistoryMapper.getJoinedById(id);
        if (history != null) {
            fillCouponHistoryJsonFields(history);
        }
        return history;
    }

    @Override
    public void markCouponUsed(Long couponHistoryId, Long orderId, String orderSn) {
        couponHistoryMapper.markUsed(couponHistoryId, orderId, orderSn, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void grantCouponToUser(String couponNote, String username, String nickname) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("优惠券发放用户不能为空");
        }
        Coupon coupon = couponMapper.getByNote(couponNote);
        if (coupon == null) {
            throw new RuntimeException("系统优惠券未初始化: " + couponNote);
        }
        Integer existing = couponHistoryMapper.countByCouponIdAndUsername(coupon.getId(), username);
        if (existing != null && existing > 0) {
            return;
        }

        CouponHistory history = new CouponHistory();
        history.setCouponId(coupon.getId());
        history.setCouponCode(generateCouponCode());
        history.setMemberUsername(username);
        history.setMemberNickname(nickname == null || nickname.trim().isEmpty() ? username : nickname);
        history.setGetType(2);
        history.setCreateTime(LocalDateTime.now());
        history.setUseStatus(0);
        couponHistoryMapper.insert(history);
    }

    private void normalizeCoupon(Coupon coupon) {
        if (coupon.getPerLimit() == null || coupon.getPerLimit() < 1) {
            coupon.setPerLimit(1);
        }
        if (coupon.getUseType() == null) {
            coupon.setUseType(0);
        }
        if (coupon.getUseType() != 2) {
            coupon.setProductRelationList(Collections.emptyList());
        }
        if (coupon.getUseType() != 1) {
            coupon.setProductCategoryRelationList(Collections.emptyList());
        }
        coupon.setProductRelationJson(writeJson(coupon.getProductRelationList()));
        coupon.setProductCategoryRelationJson(writeJson(coupon.getProductCategoryRelationList()));
    }

    private void fillCouponJsonFields(Coupon coupon) {
        if (coupon == null) {
            return;
        }
        coupon.setProductRelationList(readJsonList(coupon.getProductRelationJson()));
        coupon.setProductCategoryRelationList(readJsonList(coupon.getProductCategoryRelationJson()));
        if (coupon.getReceiveCount() == null) {
            coupon.setReceiveCount(0);
        }
        if (coupon.getUseCount() == null) {
            coupon.setUseCount(0);
        }
    }

    private void fillCouponHistoryJsonFields(CouponHistory history) {
        if (history == null) {
            return;
        }
        history.setProductRelationList(readJsonList(history.getProductRelationJson()));
        history.setProductCategoryRelationList(readJsonList(history.getProductCategoryRelationJson()));
        if ((history.getUseStatus() == null || history.getUseStatus() == 0)
                && history.getEndTime() != null
                && history.getEndTime().isBefore(LocalDateTime.now())) {
            history.setUseStatus(2);
        }
    }

    private List<Map<String, Object>> readJsonList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, LIST_MAP_TYPE);
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    private String writeJson(List<Map<String, Object>> list) {
        try {
            return objectMapper.writeValueAsString(list == null ? Collections.emptyList() : list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("优惠券关联数据保存失败");
        }
    }

    private boolean containsProduct(String json, Long productId) {
        List<Map<String, Object>> list = readJsonList(json);
        for (Map<String, Object> item : list) {
            Object value = item.get("productId");
            if (value == null) {
                continue;
            }
            try {
                if (Long.valueOf(String.valueOf(value)).equals(productId)) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return false;
    }

    private boolean containsCategory(String json, Long productId) {
        com.fsb.pojo.entity.Product product = productMapper.selectById(productId);
        if (product == null || product.getCategory() == null) {
            return false;
        }
        List<Map<String, Object>> list = readJsonList(json);
        for (Map<String, Object> item : list) {
            Object value = item.get("productCategoryId");
            if (value == null) {
                continue;
            }
            try {
                if (Long.valueOf(String.valueOf(value)).equals(Long.valueOf(product.getCategory()))) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return false;
    }

    private Map<String, Object> pageResult(List<?> list, long total) {
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return result;
    }

    private String generateCouponCode() {
        return "CP" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
