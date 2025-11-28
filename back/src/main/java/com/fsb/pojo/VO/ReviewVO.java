package com.fsb.pojo.VO;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewVO {

    private Long id;
    private Long orderId;
    private Long productId;
    private String memberUsername;
    private String content;
    private Integer star;
    private LocalDateTime createTime;
}
