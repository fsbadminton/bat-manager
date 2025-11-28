package com.fsb.Controller.user;


import com.fsb.Service.ProductService;
import com.fsb.Service.UserService;
import com.fsb.pojo.DTO.ProductPageQueryDTO;
import com.fsb.pojo.DTO.UserLoginDTO;
import com.fsb.pojo.VO.AdminLoginVO;
import com.fsb.pojo.VO.ProductVO;
import com.fsb.pojo.VO.UserLoginVO;
import com.fsb.pojo.VO.UserVO;
import com.fsb.pojo.entity.User;
import com.fsb.result.PageResult;
import com.fsb.result.Result;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) throws Exception {
        User user = userService.login(userLoginDTO);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role("user")
                .token("user-token")
                .build();
        return Result.success(userLoginVO);
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> data = new HashMap<>();
        data.put("username", "zhangsan");
        data.put("roles", Arrays.asList("user"));
        data.put("menus", new ArrayList<>());

        res.put("code", 1);
        res.put("msg", null);
        res.put("data", data);

        return res;
    }




}
