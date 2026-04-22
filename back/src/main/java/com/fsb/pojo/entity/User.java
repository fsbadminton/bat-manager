package com.fsb.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String address;
    private String email;
    private Integer status;
    private Integer isRegistered;
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

