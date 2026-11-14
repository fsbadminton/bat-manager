package com.fsb.pojo.VO;

import com.fsb.pojo.entity.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AiProductVO {
    private Long productId;
    private String brandName;
    private String name;
    private String country;
    private String website;
    private String logoUrl;
    private String imageUrl;
    private String imageUrls;
    private String description;
    private Long stock;
    private BigDecimal price;
    private Integer category;

    public static AiProductVO from(Product product) {
        AiProductVO result = new AiProductVO();
        result.setProductId(product.getProductId());
        result.setBrandName(product.getBrandName());
        result.setName(product.getName());
        result.setCountry(product.getCountry());
        result.setWebsite(product.getWebsite());
        result.setLogoUrl(product.getLogoUrl());
        result.setImageUrl(product.getImageUrl());
        result.setImageUrls(product.getImageUrls());
        result.setDescription(product.getDescription());
        result.setStock(product.getStock());
        result.setPrice(product.getPrice());
        result.setCategory(product.getCategory());
        return result;
    }
}
