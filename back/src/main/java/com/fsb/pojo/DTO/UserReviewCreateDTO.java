package com.fsb.pojo.DTO;


import lombok.Data;

@Data
public class UserReviewCreateDTO {
    private Long orderId;


    private Long productId;

    private String content;

    private Integer star;
}
