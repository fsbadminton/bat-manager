package com.fsb.Service.impl;


import com.fsb.Mapper.UserMapper;
import com.fsb.Service.UserService;
import com.fsb.pojo.DTO.UserLoginDTO;
import com.fsb.pojo.DTO.UserRegisterDTO;
import com.fsb.pojo.entity.User;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserMapper userMapper, StringRedisTemplate redisTemplate) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public User login(UserLoginDTO userLoginDTO) throws Exception {
        User user = userMapper.getByUsername(userLoginDTO.getUsername());
        if (user == null) {
            throw new Exception("用户不存在");
        }
        String raw = userLoginDTO.getPassword();
        String encoded = user.getPassword();
        boolean pass = false;
        if (encoded != null && encoded.startsWith("$2")) {
            pass = passwordEncoder.matches(raw, encoded);
        } else {
            // 兼容历史明文密码账号，首次登录后自动升级为 BCrypt
            pass = encoded != null && encoded.equals(raw);
            if (pass) {
                user.setPassword(passwordEncoder.encode(raw));
                userMapper.updatePassword(user.getId(), user.getPassword());
            }
        }
        if (!pass) {
            throw new Exception("密码错误");
        }
        return user;
    }

    @Override
    public void register(UserRegisterDTO dto) {
        if (dto == null || dto.getUsername() == null || dto.getPassword() == null || dto.getEmail() == null || dto.getCode() == null) {
            throw new RuntimeException("注册信息不完整");
        }

        if (userMapper.getByUsername(dto.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.getByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("该邮箱已注册");
        }

        String codeKey = "email:code:" + dto.getEmail();
        String storedCode = redisTemplate.opsForValue().get(codeKey);
        if (storedCode == null) {
            throw new RuntimeException("验证码已过期，请重新获取");
        }
        if (!storedCode.equals(dto.getCode())) {
            throw new RuntimeException("验证码错误");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setRole("user");
        user.setStatus(1);
        user.setIsRegistered(1);
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);

        redisTemplate.delete(codeKey);
    }
}
