package com.fsb.Service.impl;


import com.fsb.Mapper.UserMapper;
import com.fsb.Service.UserService;
import com.fsb.pojo.DTO.UserLoginDTO;
import com.fsb.pojo.VO.UserVO;
import com.fsb.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public User login(UserLoginDTO userLoginDTO) throws Exception {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        User user = userMapper.getByUsername(username);
        if(user==null){
            throw new Exception("用户不存在");
        }

        if(!user.getPassword().equals(password)){
            throw new Exception("密码错误");
        }
        return user;
    }
}
