package com.fsb.Controller.user;

import com.fsb.Service.UserOrderService;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.auth.PermissionConstants;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.OrderPageQueryDTO;
import com.fsb.pojo.DTO.UserOrderCreateDTO;
import com.fsb.result.PageResult;
import com.fsb.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/order")
public class UserOrderController {

    @Autowired
    private UserOrderService userOrderService;

    @PostMapping("/create")
    public Result<Long> createOrder(@RequestBody UserOrderCreateDTO dto) {
        AuthUser authUser = requireAuthUser();
        requirePermission(authUser, PermissionConstants.ORDER_CREATE_OWN, "Order create permission required");
        Long orderId = userOrderService.createOrder(dto, authUser.getUsername());
        return Result.success(orderId);
    }

    @GetMapping("/list")
    public Result<PageResult> list(OrderPageQueryDTO dto) {
        AuthUser authUser = requireAuthUser();
        requirePermission(authUser, PermissionConstants.ORDER_READ_OWN, "Order read permission required");
        PageResult pageResult = userOrderService.listUserOrders(authUser.getUsername(), dto);
        return Result.success(pageResult);
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
