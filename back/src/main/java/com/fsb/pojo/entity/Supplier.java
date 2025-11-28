package com.fsb.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Supplier {
    private Integer supplierID;
    private String name;
    private String phone;
    private String supplyCategory;
    private int isDeleted;

    @Override
    public String toString() {
        return "供应商:[" +
                "供应商编号:" + supplierID +
                ", 供应商名字:" + name  +
                ", 供应商电话:" + phone  +
                ", 提供类别:" + supplyCategory+"]";
    }
}
