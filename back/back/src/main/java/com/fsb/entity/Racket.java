package com.fsb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Racket {
    private Integer RacketID;
    private Integer BrandID;
    private String Model;
    private String Type;
    private String Material;
    private double Price;
    private int isDeleted;

    @Override
    public String toString() {
        return "球拍[" +
                "球拍编号:" + RacketID +
                ", 品牌编号:" + BrandID +
                ", 球拍型号:" + Model +
                ", 球拍种类:" + Type +
                ", 材质:" + Material +
                ", 价格:" + Price +
                ']';
    }
}
