package com.fsb.pojo.DTO;

import lombok.Data;

@Data
public class UserOrderUpdateDTO {
    private Long id;
    private Integer quantity;
    private String receiverName;
    private String receiverPhone;
    private String address;
}
