package com.fsb.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RacketSupplier {
    private Integer racketID;
    private Integer supplierID;

    @Override
    public String toString() {
        return "球拍供应商中间表[" +
                "球拍编号:" + racketID +
                ", 供应商编号:" + supplierID +
                ']';
    }
}
