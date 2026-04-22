package com.fsb.pojo.DTO;


import lombok.Data;

@Data
public class OmsReturnApplyDTO {
    private Integer status; // 新的状态：1->确认退货/处理中; 2->已完成; 3->已拒绝

    private String handleMan; // 处理人
    private String handleNote; // 处理备注
    private String companyAddress;
}
