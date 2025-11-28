package com.fsb.pojo.VO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginVO {

    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String token;
    private String role;
}
