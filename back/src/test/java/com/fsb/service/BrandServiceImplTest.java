package com.fsb.service;

import com.fsb.Mapper.BrandMapper;
import com.fsb.Service.impl.BrandServiceImpl;
import com.fsb.pojo.entity.Brand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandServiceImplTest {

    @Mock
    private BrandMapper brandMapper;

    @InjectMocks
    private BrandServiceImpl service;

    @Test
    void deleteRejectsBrandReferencedByNonDeletedProduct() {
        when(brandMapper.getById(1L)).thenReturn(Brand.builder().id(1L).name("Yonex").build());
        when(brandMapper.countRelatedProducts("Yonex")).thenReturn(1);

        assertThrows(IllegalArgumentException.class, () -> service.delete(1L));
        verify(brandMapper, never()).delete(1L);
    }

    @Test
    void deleteRemovesBrandWhenNoProductReferencesIt() {
        when(brandMapper.getById(1L)).thenReturn(Brand.builder().id(1L).name("Yonex").build());
        when(brandMapper.countRelatedProducts("Yonex")).thenReturn(0);

        assertDoesNotThrow(() -> service.delete(1L));
        verify(brandMapper).delete(1L);
    }
}
