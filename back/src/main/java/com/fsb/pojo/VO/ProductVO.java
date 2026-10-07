package com.fsb.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductVO {
    private Long productId;
    private String brandName;
    private String name;
    private String country;
    private String website;
    private Integer isDeleted;
    private String logoUrl;       // 品牌 logo

    private String imageUrl;        // 主图片URL
    private String imageUrls;       // 多图片URL(逗号分隔)
    private String description;   // 品牌简介
    private Long stock;//库存
    private Long sale;
    private BigDecimal price;
    private String productSn;       //货号
    private Integer recommendStatus;
    private Integer newStatus;
    private Integer publishStatus;
    private Integer category;
    private Integer auditStatus;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
