package com.fsb.pojo.DTO;


import lombok.Data;

@Data
public class BrandPageQueryDTO {
    private Integer pageNum;   // 当前页码
    private Integer pageSize;  // 每页大小

    private String name;
}
