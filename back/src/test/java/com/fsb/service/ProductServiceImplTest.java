package com.fsb.service;

import com.fsb.Mapper.BrandMapper;
import com.fsb.Mapper.CategoryMapper;
import com.fsb.Mapper.ProductMapper;
import com.fsb.Service.impl.ProductServiceImpl;
import com.fsb.pojo.DTO.ProductDTO;
import com.fsb.pojo.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private BrandMapper brandMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private ProductServiceImpl service;

    @Test
    void statusUpdateDoesNotOverwritePrice() {
        ProductDTO dto = new ProductDTO();
        dto.setPublishStatus(1);

        service.updateStatus(dto, 20L);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productMapper).update(captor.capture());
        assertEquals(20L, captor.getValue().getProductId());
        assertEquals(1, captor.getValue().getPublishStatus());
        assertNull(captor.getValue().getPrice());
    }

    @Test
    void productUpdateKeepsSubmittedDecimalPrice() {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(20L);
        dto.setPrice(new BigDecimal("1299.90"));

        service.update(dto);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productMapper).update(captor.capture());
        assertEquals(new BigDecimal("1299.90"), captor.getValue().getPrice());
    }
}
