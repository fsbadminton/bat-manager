package com.fsb.pojo.DTO;


import lombok.Data;

@Data
public class UserReviewUpdateDTO {
    private Long id;
    private String memberUsername;
    private String content;
    private Integer star;
}
