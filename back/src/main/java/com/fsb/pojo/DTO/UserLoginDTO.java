package com.fsb.pojo.DTO;


import lombok.Data;

@Data
public class UserLoginDTO {
    private String username;
    private String password;
    private String nickname;
    private String phone;

}
