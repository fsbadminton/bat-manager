package com.fsb.pojo.DTO;

import lombok.Data;

@Data
public class UserReviewPageQueryDTO {

    private Integer pageNum;   // 页码
    private Integer pageSize; // 每页数量

    private String username;
    private Integer isDeleted;

    // 可选筛选条件：
    private Long productId;   // 想查某个商品的评论
    private String productName;
    private Integer star;     // 星级过滤
}
