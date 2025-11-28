package com.fsb.pojo.VO;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReturnReasonVO {

    private Long id;

    private String name;


    private LocalDateTime createTime;
}
