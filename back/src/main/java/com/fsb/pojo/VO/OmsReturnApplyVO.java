package com.fsb.pojo.VO;

import com.fsb.pojo.entity.Order;
import com.fsb.pojo.entity.OrderItem;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OmsReturnApplyVO {
    private Long id;
    private Long orderId;
    private Long productId;
    private String username;
    private BigDecimal productRealPrice;
    private Integer productCount;
    private BigDecimal returnAmount;
    private String receiverName;
    private String receiverPhone;
    private Integer status; // 0->待处理; 1->退货中; 2->已完成; 3->已拒绝
    private String companyAddress;
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createTime;
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime handleTime;
    //private LocalDateTime updateTime;
    private String reason;
    private String description;
    private String proofPics;

    private Order order;
    private List<OrderItem> orderItems;

    private String handleNote;
    private String handleMan;
    private String receiveMan;
    private LocalDateTime receiveTime;
}
