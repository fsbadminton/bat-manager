package com.fsb.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    private Long id;

    private Long orderId;
    private Long productId;
    private String memberUsername;
    private String content;
    private Integer star; // 评分 1-5

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted; // 0未删除 1已删除
}
