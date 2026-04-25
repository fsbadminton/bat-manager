package com.fsb.Controller.user;

import com.fsb.Service.CouponService;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.auth.PermissionConstants;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.UserCouponPageQueryDTO;
import com.fsb.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user/coupon")
public class UserCouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/available")
    public Result<Map<String, Object>> available(UserCouponPageQueryDTO dto) {
        AuthUser authUser = requireAuthUser();
        requirePermission(authUser, PermissionConstants.COUPON_READ_OWN, "Coupon read permission required");
        return Result.success(couponService.pageAvailableForUser(authUser.getUsername(), dto));
    }

    @GetMapping("/my")
    public Result<Map<String, Object>> my(UserCouponPageQueryDTO dto) {
        AuthUser authUser = requireAuthUser();
        requirePermission(authUser, PermissionConstants.COUPON_READ_OWN, "Coupon read permission required");
        return Result.success(couponService.pageUserCoupons(authUser.getUsername(), dto));
    }

    @PostMapping("/claim/{couponId}")
    public Result<Void> claim(@PathVariable Long couponId) {
        AuthUser authUser = requireAuthUser();
        requirePermission(authUser, PermissionConstants.COUPON_CLAIM_OWN, "Coupon claim permission required");
        couponService.claimCoupon(couponId, authUser.getUsername());
        return Result.success();
    }

    private AuthUser requireAuthUser() {
        AuthUser authUser = AuthContext.getCurrentUser();
        if (authUser == null) {
            throw new AuthException(401, "Unauthorized");
        }
        return authUser;
    }

    private void requirePermission(AuthUser authUser, String permission, String message) {
        if (!authUser.hasPermission(permission)) {
            throw new AuthException(403, message);
        }
    }
}
