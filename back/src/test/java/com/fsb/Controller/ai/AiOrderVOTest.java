package com.fsb.Controller.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsb.pojo.VO.AiOrderVO;
import com.fsb.pojo.entity.Order;
import com.fsb.pojo.entity.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiOrderVOTest {
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void orderViewExcludesRecipientDataAndInternalNotes() throws Exception {
        OrderItem item = new OrderItem();
        item.setProductId(7L);
        item.setProductName("Racket");
        item.setProductSn("INTERNAL-SKU");
        item.setProductQuantity(1);
        item.setProductPrice(new BigDecimal("120.00"));
        item.setProductTotal(new BigDecimal("120.00"));

        Order order = new Order();
        order.setId(9L);
        order.setOrderSn("ORDER-9");
        order.setReceiverName("Private Name");
        order.setReceiverPhone("Private Phone");
        order.setAddress("Private Address");
        order.setNote("Private User Note");
        order.setAdminNote("Private Admin Note");
        order.setDeliverySn("PRIVATE-TRACKING-CODE");
        order.setOrderItems(List.of(item));

        String json = objectMapper.writeValueAsString(AiOrderVO.from(order));

        assertTrue(json.contains("ORDER-9"));
        assertTrue(json.contains("Racket"));
        assertFalse(json.contains("Private Name"));
        assertFalse(json.contains("Private Phone"));
        assertFalse(json.contains("Private Address"));
        assertFalse(json.contains("Private User Note"));
        assertFalse(json.contains("Private Admin Note"));
        assertFalse(json.contains("PRIVATE-TRACKING-CODE"));
        assertFalse(json.contains("INTERNAL-SKU"));
    }
}
