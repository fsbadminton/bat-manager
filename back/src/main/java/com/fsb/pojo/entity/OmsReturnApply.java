package com.fsb.pojo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OmsReturnApply {

    private Long id;
    private Long orderId;
    private Long productId;
    private String username;
    private Long userId;

    private BigDecimal productRealPrice;
    private Integer productCount;

    private BigDecimal returnAmount;  //returnAmount=productRealPrice*productCount
    private String returnName;
    private String returnPhone;
    private Integer status; // 0->待处理; 1->退货中; 2->已完成; 3->已拒绝

    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createTime;

    //private LocalDateTime updateTime;
    private String reason;
    private String description;
    private String proofPics;
    private String companyAddress;
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime handleTime;
    private String handleNote;
    private String handleMan;

    private String receiveMan;
    private LocalDateTime receiveTime;
    // 简化: 可以在实体类中加入商品信息，但通常通过关联查询获取
    // private String productName;
    // private String productSku;

}
