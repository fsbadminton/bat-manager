package com.fsb.Controller.ai;

import com.fsb.Service.CouponService;
import com.fsb.Service.ProductService;
import com.fsb.Service.UserOrderService;
import com.fsb.pojo.DTO.OrderPageQueryDTO;
import com.fsb.pojo.DTO.ProductPageQueryDTO;
import com.fsb.pojo.DTO.UserCouponPageQueryDTO;
import com.fsb.pojo.entity.Product;
import com.fsb.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Internal, read-only business surface for the Nexus MCP server.
 * AuthInterceptor requires the separate AI service token before this controller is reached.
 */
@RestController
@RequestMapping("/internal/ai")
public class AiInternalController {

    private final ProductService productService;
    private final UserOrderService userOrderService;
    private final CouponService couponService;

    public AiInternalController(ProductService productService,
                                UserOrderService userOrderService,
                                CouponService couponService) {
        this.productService = productService;
        this.userOrderService = userOrderService;
        this.couponService = couponService;
    }

    @GetMapping("/products")
    public Result<?> products(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brandName,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ProductPageQueryDTO query = new ProductPageQueryDTO();
        query.setPageNum(1);
        query.setPageSize(Math.min(Math.max(pageSize, 1), 20));
        query.setName(blankToNull(name));
        query.setBrandName(blankToNull(brandName));
        query.setCategoryId(categoryId);
        return Result.success(productService.pageQueryByUser(query));
    }

    @GetMapping("/products/{id}")
    public Result<?> product(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null || !Integer.valueOf(0).equals(product.getIsDeleted())
                || !Integer.valueOf(1).equals(product.getPublishStatus())) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    @GetMapping("/orders")
    public Result<?> orders(
            @RequestHeader("X-AI-Username") String username,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        OrderPageQueryDTO query = new OrderPageQueryDTO();
        query.setPageNum(1);
        query.setPageSize(Math.min(Math.max(pageSize, 1), 20));
        return Result.success(userOrderService.listUserOrders(username, query));
    }

    @GetMapping("/orders/{id}")
    public Result<?> order(@PathVariable Long id, @RequestHeader("X-AI-Username") String username) {
        return Result.success(userOrderService.getUserOrderDetail(id, username));
    }

    @GetMapping("/coupons")
    public Result<?> coupons(
            @RequestHeader("X-AI-Username") String username,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        UserCouponPageQueryDTO query = new UserCouponPageQueryDTO();
        query.setPageNum(1);
        query.setPageSize(Math.min(Math.max(pageSize, 1), 20));
        return Result.success(couponService.pageUserCoupons(username, query));
    }

    @GetMapping("/return-policy")
    public Result<Map<String, Object>> returnPolicy() {
        Map<String, Object> policy = new LinkedHashMap<>();
        policy.put("summary", "已完成订单可在用户端提交退货申请，申请后由管理员审核。");
        policy.put("entry", "/client/returns");
        policy.put("note", "具体审核结果和时效以订单状态及人工审核为准。");
        return Result.success(policy);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
