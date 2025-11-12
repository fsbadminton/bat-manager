package com.fsb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    private Integer BrandID;
    private String Name;
    private String Country;
    private String Website;
    private int isDeleted;

    /*public Brand(String Name, String Country, String Website,int isDeleted) {
        this.Name = Name;
        this.Country = Country;
        this.Website = Website;
        this.isDeleted = isDeleted;
    }*/

    @Override
    public String toString() {
        return "品牌:[" +
                "品牌编号:" + BrandID +
                ", 品牌名:" + Name +
                ", 产地:" + Country +
                ", 网址:" + Website+"]";
    }
}
