package com.fsb.pojo.DTO;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OmsReturnApplyPageQueryDTO {

    private Long id; // 退货申请编号
    private Integer status; // 处理状态
    private String handleMan; // 处理人
    private String returnName; // 申请人姓名
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createTime;
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate handleTime;

    //申请时间
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    //处理时间
    private LocalDateTime startTime1;
    private LocalDateTime endTime1;

    // 分页参数 (前端通常以单独参数传入，但也可集成到此)
    private Integer pageSize;
    private Integer pageNum;
}
