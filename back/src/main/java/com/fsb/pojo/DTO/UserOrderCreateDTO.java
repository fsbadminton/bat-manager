package com.fsb.pojo.DTO;

import lombok.Data;

@Data
public class UserOrderCreateDTO {

    private Long productId;
    private Integer quantity;
    private Long couponHistoryId;

    private String memberUsername;
    private String receiverName;
    private String receiverPhone;
    private String address;
    private String note;
}
