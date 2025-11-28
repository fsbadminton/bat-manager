package com.fsb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
    private Integer inventoryID;
    private Integer racketID;
    private Integer stock;
    private String warehouseLocation;
    private int isDeleted;

    @Override
    public String toString() {
        return "库存[" +
                "库存表编号:"+ inventoryID+
                ", 球拍编号:" + racketID +
                ", 剩余:" + stock +
                ", 仓库位置:" + warehouseLocation +
                ']';
    }
}
