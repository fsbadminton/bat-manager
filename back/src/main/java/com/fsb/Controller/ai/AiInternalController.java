package com.fsb.Controller.ai;

import com.fsb.Service.CouponService;
import com.fsb.Service.ProductService;
import com.fsb.Service.PlatformIdentityService;
import com.fsb.auth.AiContextSignature;
import com.fsb.auth.AiRequestReplayGuard;
import com.fsb.Service.UserOrderService;
import com.fsb.Service.AiSupportTicketService;
import com.fsb.Mapper.UserMapper;
import com.fsb.pojo.DTO.AiTicketCreateRequest;
import com.fsb.pojo.DTO.OrderPageQueryDTO;
import com.fsb.pojo.DTO.ProductPageQueryDTO;
import com.fsb.pojo.DTO.UserCouponPageQueryDTO;
import com.fsb.pojo.VO.AiOrderPageVO;
import com.fsb.pojo.VO.AiOrderVO;
import com.fsb.pojo.VO.AiCouponPageVO;
import com.fsb.pojo.VO.AiProductPageVO;
import com.fsb.pojo.VO.AiProductVO;
import com.fsb.pojo.entity.Product;
import com.fsb.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final PlatformIdentityService identityService;
    private final AiContextSignature contextSignature;
    private final AiRequestReplayGuard replayGuard;
    private final UserMapper userMapper;
    private final AiSupportTicketService supportTicketService;

    public AiInternalController(ProductService productService,
                                UserOrderService userOrderService,
                                CouponService couponService,
                                PlatformIdentityService identityService,
                                AiContextSignature contextSignature,
                                AiRequestReplayGuard replayGuard,
                                UserMapper userMapper,
                                AiSupportTicketService supportTicketService) {
        this.productService = productService;
        this.userOrderService = userOrderService;
        this.couponService = couponService;
        this.identityService = identityService;
        this.contextSignature = contextSignature;
        this.replayGuard = replayGuard;
        this.userMapper = userMapper;
        this.supportTicketService = supportTicketService;
    }

    @GetMapping("/products")
    public Result<?> products(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brandName,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        validateSearchTerm("name", name);
        validateSearchTerm("brandName", brandName);
        if (categoryId != null && categoryId < 1) {
            throw new IllegalArgumentException("categoryId must be a positive integer");
        }
        validatePageSize(pageSize);
        ProductPageQueryDTO query = new ProductPageQueryDTO();
        query.setPageNum(1);
        query.setPageSize(pageSize);
        query.setName(blankToNull(name));
        query.setBrandName(blankToNull(brandName));
        query.setCategoryId(categoryId);
        return Result.success(AiProductPageVO.from(productService.pageQueryByUser(query)));
    }

    @GetMapping("/products/{id}")
    public Result<?> product(@PathVariable Long id) {
        validateId("product_id", id);
        Product product = productService.getById(id);
        if (product == null || !Integer.valueOf(0).equals(product.getIsDeleted())
                || !Integer.valueOf(1).equals(product.getPublishStatus())) {
            return Result.fail(404, "商品不存在", "NOT_FOUND");
        }
        return Result.success(AiProductVO.from(product));
    }

    @GetMapping("/orders")
    public Result<?> orders(
            @RequestHeader(value = "X-AI-Platform", required = false) String platform,
            @RequestHeader(value = "X-AI-External-User-Id", required = false) String externalUserId,
            @RequestHeader(value = "X-AI-Is-DM", required = false) Boolean isDm,
            @RequestHeader(value = "X-AI-Context-Timestamp", required = false) String contextTimestamp,
            @RequestHeader(value = "X-AI-Context-Nonce", required = false) String contextNonce,
            @RequestHeader(value = "X-AI-Context-Signature", required = false) String contextSignatureValue,
            @RequestHeader(value = "X-AI-Web-User-Id", required = false) String webUserId,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        validatePageSize(pageSize);
        String resolvedUsername = resolveUsername(platform, externalUserId, isDm, contextTimestamp, contextNonce, contextSignatureValue, webUserId);
        OrderPageQueryDTO query = new OrderPageQueryDTO();
        query.setPageNum(1);
        query.setPageSize(pageSize);
        return Result.success(AiOrderPageVO.from(userOrderService.listUserOrders(resolvedUsername, query)));
    }

    @GetMapping("/orders/{id}")
    public Result<?> order(@PathVariable Long id,
                           @RequestHeader(value = "X-AI-Platform", required = false) String platform,
                           @RequestHeader(value = "X-AI-External-User-Id", required = false) String externalUserId,
                           @RequestHeader(value = "X-AI-Is-DM", required = false) Boolean isDm,
                           @RequestHeader(value = "X-AI-Context-Timestamp", required = false) String contextTimestamp,
                           @RequestHeader(value = "X-AI-Context-Nonce", required = false) String contextNonce,
                           @RequestHeader(value = "X-AI-Context-Signature", required = false) String contextSignatureValue,
                           @RequestHeader(value = "X-AI-Web-User-Id", required = false) String webUserId) {
        validateId("order_id", id);
        return Result.success(AiOrderVO.from(userOrderService.getUserOrderDetail(id,
                resolveUsername(platform, externalUserId, isDm, contextTimestamp, contextNonce, contextSignatureValue, webUserId))));
    }

    @GetMapping("/coupons")
    public Result<?> coupons(
            @RequestHeader(value = "X-AI-Platform", required = false) String platform,
            @RequestHeader(value = "X-AI-External-User-Id", required = false) String externalUserId,
            @RequestHeader(value = "X-AI-Is-DM", required = false) Boolean isDm,
            @RequestHeader(value = "X-AI-Context-Timestamp", required = false) String contextTimestamp,
            @RequestHeader(value = "X-AI-Context-Nonce", required = false) String contextNonce,
            @RequestHeader(value = "X-AI-Context-Signature", required = false) String contextSignatureValue,
            @RequestHeader(value = "X-AI-Web-User-Id", required = false) String webUserId,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        validatePageSize(pageSize);
        String resolvedUsername = resolveUsername(platform, externalUserId, isDm, contextTimestamp, contextNonce, contextSignatureValue, webUserId);
        UserCouponPageQueryDTO query = new UserCouponPageQueryDTO();
        query.setPageNum(1);
        query.setPageSize(pageSize);
        return Result.success(AiCouponPageVO.from(couponService.pageUserCoupons(resolvedUsername, query)));
    }

    @GetMapping("/return-policy")
    public Result<Map<String, Object>> returnPolicy() {
        Map<String, Object> policy = new LinkedHashMap<>();
        policy.put("summary", "已完成订单可在用户端提交退货申请，申请后由管理员审核。");
        policy.put("entry", "/client/returns");
        policy.put("note", "具体审核结果和时效以订单状态及人工审核为准。");
        return Result.success(policy);
    }

    @PostMapping("/tickets")
    public Result<?> createTicket(@RequestBody @jakarta.validation.Valid AiTicketCreateRequest request,
                                  @RequestHeader(value = "X-AI-Platform", required = false) String platform,
                                  @RequestHeader(value = "X-AI-External-User-Id", required = false) String externalUserId,
                                  @RequestHeader(value = "X-AI-Is-DM", required = false) Boolean isDm,
                                  @RequestHeader(value = "X-AI-Context-Timestamp", required = false) String contextTimestamp,
                                  @RequestHeader(value = "X-AI-Context-Nonce", required = false) String contextNonce,
                                  @RequestHeader(value = "X-AI-Context-Signature", required = false) String contextSignatureValue,
                                  @RequestHeader(value = "X-AI-Web-User-Id", required = false) String webUserId) {
        Long userId = resolveUserId(platform, externalUserId, isDm, contextTimestamp, contextNonce,
                contextSignatureValue, webUserId);
        return Result.success(supportTicketService.create(userId, request));
    }

    @GetMapping("/tickets")
    public Result<?> tickets(
            @RequestHeader(value = "X-AI-Platform", required = false) String platform,
            @RequestHeader(value = "X-AI-External-User-Id", required = false) String externalUserId,
            @RequestHeader(value = "X-AI-Is-DM", required = false) Boolean isDm,
            @RequestHeader(value = "X-AI-Context-Timestamp", required = false) String contextTimestamp,
            @RequestHeader(value = "X-AI-Context-Nonce", required = false) String contextNonce,
            @RequestHeader(value = "X-AI-Context-Signature", required = false) String contextSignatureValue,
            @RequestHeader(value = "X-AI-Web-User-Id", required = false) String webUserId,
            @RequestParam(defaultValue = "20") Integer limit) {
        if (limit == null || limit < 1 || limit > 20) throw new IllegalArgumentException("limit must be between 1 and 20");
        return Result.success(supportTicketService.list(resolveUserId(platform, externalUserId, isDm,
                contextTimestamp, contextNonce, contextSignatureValue, webUserId), limit));
    }

    @GetMapping("/tickets/{ticketNo}")
    public Result<?> ticket(@PathVariable String ticketNo,
                            @RequestHeader(value = "X-AI-Platform", required = false) String platform,
                            @RequestHeader(value = "X-AI-External-User-Id", required = false) String externalUserId,
                            @RequestHeader(value = "X-AI-Is-DM", required = false) Boolean isDm,
                            @RequestHeader(value = "X-AI-Context-Timestamp", required = false) String contextTimestamp,
                            @RequestHeader(value = "X-AI-Context-Nonce", required = false) String contextNonce,
                            @RequestHeader(value = "X-AI-Context-Signature", required = false) String contextSignatureValue,
                            @RequestHeader(value = "X-AI-Web-User-Id", required = false) String webUserId) {
        if (ticketNo == null || !ticketNo.matches("[A-Za-z0-9-]{3,24}")) {
            throw new IllegalArgumentException("ticket_no format is invalid");
        }
        return Result.success(supportTicketService.get(resolveUserId(platform, externalUserId, isDm,
                contextTimestamp, contextNonce, contextSignatureValue, webUserId), ticketNo));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void validateSearchTerm(String name, String value) {
        if (value != null && value.length() > 100) {
            throw new IllegalArgumentException(name + " must be at most 100 characters");
        }
    }

    private void validatePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1 || pageSize > 20) {
            throw new IllegalArgumentException("page_size must be between 1 and 20");
        }
    }

    private void validateId(String name, Long id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException(name + " must be a positive integer");
        }
    }

    private String resolveUsername(String platform, String externalUserId, Boolean isDm,
                                   String timestamp, String nonce, String signature, String webUserId) {
        boolean hasPlatform = platform != null && !platform.isBlank();
        boolean hasExternalUserId = externalUserId != null && !externalUserId.isBlank();
        boolean hasWebUserId = webUserId != null && !webUserId.isBlank();
        if (hasWebUserId && (hasPlatform || hasExternalUserId)) {
            throw new com.fsb.exception.AuthException(401, "INVALID_CONTEXT: ambiguous AI identity context");
        }
        if (hasPlatform != hasExternalUserId) {
            throw new com.fsb.exception.AuthException(401, "INVALID_CONTEXT: incomplete platform identity context");
        }
        if (hasPlatform) {
            if (!"telegram".equalsIgnoreCase(platform) && !"qq".equalsIgnoreCase(platform)) {
                throw new com.fsb.exception.AuthException(401, "INVALID_CONTEXT: unsupported platform");
            }
            boolean privateChat = Boolean.TRUE.equals(isDm);
            contextSignature.verifyIdentity(platform, externalUserId, privateChat, timestamp, nonce, signature);
            replayGuard.consume(nonce);
            if (!privateChat) {
                throw new com.fsb.exception.AuthException(403, "FORBIDDEN: personal data is private-chat only");
            }
            return identityService.resolveUsername(platform, externalUserId);
        }
        if (hasWebUserId) {
            contextSignature.verifyWebUser(webUserId, timestamp, nonce, signature);
            replayGuard.consume(nonce);
            Long userId;
            try {
                userId = Long.valueOf(webUserId);
            } catch (NumberFormatException ex) {
                throw new com.fsb.exception.AuthException(401, "INVALID_CONTEXT: invalid web user id");
            }
            var user = userMapper.getById(userId);
            if (user == null || (user.getStatus() != null && user.getStatus() == 0)) {
                throw new IllegalStateException("商城用户不存在或已被禁用");
            }
            return user.getUsername();
        }
        throw new com.fsb.exception.AuthException(401, "INVALID_CONTEXT: signed AI identity required");
    }

    private Long resolveUserId(String platform, String externalUserId, Boolean isDm,
                               String timestamp, String nonce, String signature, String webUserId) {
        boolean hasPlatform = platform != null && !platform.isBlank();
        boolean hasExternal = externalUserId != null && !externalUserId.isBlank();
        boolean hasWeb = webUserId != null && !webUserId.isBlank();
        if (hasWeb && (hasPlatform || hasExternal) || hasPlatform != hasExternal) {
            throw new com.fsb.exception.AuthException(401, "INVALID_CONTEXT: ambiguous AI identity context");
        }
        if (hasPlatform) {
            if (!"telegram".equalsIgnoreCase(platform) && !"qq".equalsIgnoreCase(platform)) {
                throw new com.fsb.exception.AuthException(401, "INVALID_CONTEXT: unsupported platform");
            }
            boolean privateChat = Boolean.TRUE.equals(isDm);
            contextSignature.verifyIdentity(platform, externalUserId, privateChat, timestamp, nonce, signature);
            replayGuard.consume(nonce);
            if (!privateChat) throw new com.fsb.exception.AuthException(403, "FORBIDDEN: tickets are private-chat only");
            return identityService.resolveAiIdentity(platform, externalUserId).getCustomerId();
        }
        if (hasWeb) {
            contextSignature.verifyWebUser(webUserId, timestamp, nonce, signature);
            replayGuard.consume(nonce);
            try {
                Long userId = Long.valueOf(webUserId);
                var user = userMapper.getById(userId);
                if (user == null || (user.getStatus() != null && user.getStatus() == 0)) {
                    throw new com.fsb.exception.AuthException(401, "INVALID_CONTEXT: web user is unavailable");
                }
                return userId;
            } catch (NumberFormatException ex) {
                throw new com.fsb.exception.AuthException(401, "INVALID_CONTEXT: invalid web user id");
            }
        }
        throw new com.fsb.exception.AuthException(401, "INVALID_CONTEXT: signed AI identity required");
    }
}
