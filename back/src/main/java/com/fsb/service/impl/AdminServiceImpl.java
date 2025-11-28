package com.fsb.Service.impl;

import com.fsb.Mapper.AdminMapper;
import com.fsb.Service.AdminService;
import com.fsb.pojo.DTO.AdminLoginDTO;
import com.fsb.pojo.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;


    /**
     * 管理员登录
     * @param adminLoginDTO
     * @return
     */
    @Override
    public Admin login(AdminLoginDTO adminLoginDTO) throws Exception {
        String username = adminLoginDTO.getUsername();
        String password = adminLoginDTO.getPassword();

        Admin admin = adminMapper.getByUsername(username);
        if(admin==null){
            throw new Exception("管理员不存在");
        }

        if(!admin.getPassword().equals(password)){
            throw new Exception("密码错误");
        }

        return admin;
    }
}
