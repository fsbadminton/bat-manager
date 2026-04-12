package com.fsb.pojo.VO;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminLoginVO {


    private Long id;
    private String username;
    private String name;
    private String token;
    private String role;
    private List<String> permissions;
}
