package com.fsb.pojo.DTO;

import lombok.Data;

@Data
public class ProductPageQueryDTO {
    // 分页参数
    private Integer pageNum;   // 当前页码
    private Integer pageSize;  // 每页大小

    // 查询条件
    private String brandName;
    private Long brandId;  // 品牌id
    private String categoryName;
    private Long categoryId;

    private Long stock;//库存
    private String name;
    private String country;
    private Integer isDeleted;
    private String productSn;       //货号
    private Integer publishStatus;

    private Integer category;  // 1. 羽毛球拍 2.网球拍 3.乒乓球拍
    private Integer auditStatus;// 0.未审核 1.审核通过

}
