package com.fsb.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private Integer CustomerID;
    private String Name;
    private String Phone;
    private String Address;
    private Date RegisterDate;
    private String Password;
    private int isDeleted;

    public Customer(String name, String password) {
        Name = name;
        Password = password;
    }

    @Override
    public String toString() {
        return "客户[" +
                "客户编号:" + CustomerID +
                ", 姓名:" + Name +
                ", 电话:" + Phone +
                ", 地址:" + Address +
                ", 注册时间:" + RegisterDate +
                ']';
    }
}
