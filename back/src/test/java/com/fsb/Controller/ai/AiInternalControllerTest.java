package com.fsb.Controller.ai;

import com.fsb.Mapper.UserMapper;
import com.fsb.Service.CouponService;
import com.fsb.Service.PlatformIdentityService;
import com.fsb.Service.ProductService;
import com.fsb.Service.UserOrderService;
import com.fsb.Service.AiSupportTicketService;
import com.fsb.auth.AiContextSignature;
import com.fsb.auth.AiRequestReplayGuard;
import com.fsb.exception.AuthException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class AiInternalControllerTest {
    @Test
    void productSearchRejectsOutOfContractPageSizeBeforeCallingService() {
        ProductService products = mock(ProductService.class);
        AiInternalController controller = controller(products);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.products(null, null, null, 21));

        assertTrue(exception.getMessage().contains("page_size"));
        verify(products, never()).pageQueryByUser(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void productSearchRejectsLongKeywordAndInvalidCategory() {
        ProductService products = mock(ProductService.class);
        AiInternalController controller = controller(products);

        assertThrows(IllegalArgumentException.class,
                () -> controller.products("x".repeat(101), null, null, 10));
        assertThrows(IllegalArgumentException.class,
                () -> controller.products(null, null, 0L, 10));
        verify(products, never()).pageQueryByUser(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void productDetailRejectsNonPositiveIdBeforeCallingService() {
        ProductService products = mock(ProductService.class);
        AiInternalController controller = controller(products);

        assertThrows(IllegalArgumentException.class, () -> controller.product(0L));

        verify(products, never()).getById(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void groupPersonalOrderLookupIsRejectedBeforeTouchingOrderService() {
        ProductService products = mock(ProductService.class);
        UserOrderService orders = mock(UserOrderService.class);
        CouponService coupons = mock(CouponService.class);
        PlatformIdentityService identities = mock(PlatformIdentityService.class);
        AiContextSignature signatures = mock(AiContextSignature.class);
        AiRequestReplayGuard replayGuard = mock(AiRequestReplayGuard.class);
        UserMapper users = mock(UserMapper.class);
        AiInternalController controller = new AiInternalController(
                products, orders, coupons, identities, signatures, replayGuard, users, mock(AiSupportTicketService.class));

        AuthException exception = assertThrows(AuthException.class,
                () -> controller.orders("telegram", "external-1", false,
                        "timestamp", "nonce", "signature", null, 10));

        assertEquals(403, exception.getCode());
        verify(signatures).verifyIdentity("telegram", "external-1", false,
                "timestamp", "nonce", "signature");
        verify(orders, never()).listUserOrders(org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void mixedWebAndPlatformIdentityIsRejected() {
        ProductService products = mock(ProductService.class);
        UserOrderService orders = mock(UserOrderService.class);
        CouponService coupons = mock(CouponService.class);
        PlatformIdentityService identities = mock(PlatformIdentityService.class);
        AiContextSignature signatures = mock(AiContextSignature.class);
        AiRequestReplayGuard replayGuard = mock(AiRequestReplayGuard.class);
        UserMapper users = mock(UserMapper.class);
        AiInternalController controller = new AiInternalController(
                products, orders, coupons, identities, signatures, replayGuard, users, mock(AiSupportTicketService.class));

        AuthException exception = assertThrows(AuthException.class,
                () -> controller.orders("telegram", "external-1", true,
                        "timestamp", "nonce", "signature", "42", 10));

        assertEquals(401, exception.getCode());
        verify(signatures, never()).verifyIdentity(
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyBoolean(), org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
        verify(signatures, never()).verifyWebUser(
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    private AiInternalController controller(ProductService products) {
        return new AiInternalController(products, mock(UserOrderService.class), mock(CouponService.class),
                mock(PlatformIdentityService.class), mock(AiContextSignature.class),
                mock(AiRequestReplayGuard.class), mock(UserMapper.class), mock(AiSupportTicketService.class));
    }
}
