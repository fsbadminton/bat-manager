package com.fsb.Controller.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsb.pojo.VO.AiProductVO;
import com.fsb.pojo.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiProductVOTest {
    @Test
    void productViewContainsDisplayDataWithoutInternalPublicationFlags() throws Exception {
        Product product = new Product();
        product.setProductId(5L);
        product.setName("Racket");
        product.setBrandName("Brand");
        product.setPrice(new BigDecimal("120.00"));
        product.setStock(4L);
        product.setProductSn("INTERNAL-SKU");
        product.setIsDeleted(0);
        product.setAuditStatus(1);
        product.setPublishStatus(1);

        String json = new ObjectMapper().writeValueAsString(AiProductVO.from(product));

        assertTrue(json.contains("Racket"));
        assertTrue(json.contains("120.00"));
        assertFalse(json.contains("INTERNAL-SKU"));
        assertFalse(json.contains("isDeleted"));
        assertFalse(json.contains("auditStatus"));
        assertFalse(json.contains("publishStatus"));
    }
}
