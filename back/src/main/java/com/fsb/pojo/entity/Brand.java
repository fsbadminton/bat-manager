package com.fsb.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    private Long id;
    private String name;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Integer productCount; // 新增球拍数量
    private Integer reviewCount;  // 新增评论数量

}
