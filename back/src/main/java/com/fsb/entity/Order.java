package com.fsb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Integer orderID;
    private Integer customerID;
    private Date orderDate;
    private double total;
    private int isDeleted;

    @Override
    public String toString() {
        return "订单[" +
                "订单编号:" + orderID +
                ", 客户编号:" + customerID +
                ", 下单时间:" + orderDate +
                ", 价格:" + total +
                ']';
    }
}
