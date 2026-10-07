package com.fsb.pojo.DTO;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long productId;
    private String brandName;
    private String name;
    private String country;
    private String website;
    private Integer isDeleted;
    private String logoUrl;       // 品牌 logo
    private String description;   // 品牌简介
    private Long stock;//库存

    private String imageUrl;
    private String imageUrls;

    private Long sale;
    private BigDecimal price;
    private String productSn;       //货号

    private Integer category;
    private Integer auditStatus;

    private Integer newStatus;
    private Integer publishStatus;
    private Integer recommendStatus;
}
