package com.fsb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private Integer orderID;
    private Integer racketID;
    private Integer quantity;
    private double price;

    @Override
    public String toString() {
        return "订单明细[" +
                "订单编号:" + orderID +
                ", 球拍编号:" + racketID +
                ", 购买数:" + quantity +
                ", 价格:" + price +
                ']';
    }
}
