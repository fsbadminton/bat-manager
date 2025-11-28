package com.fsb.pojo.VO;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminLoginVO {


    private Long id;
    private String username;
    private String name;
    private String token;
    private String role;
}
