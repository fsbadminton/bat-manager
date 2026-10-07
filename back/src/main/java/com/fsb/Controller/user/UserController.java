package com.fsb.Controller.user;

import com.fsb.Service.UserService;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.auth.RolePermissionService;
import com.fsb.pojo.DTO.UserLoginDTO;
import com.fsb.pojo.DTO.UserRegisterDTO;
import com.fsb.pojo.VO.UserLoginVO;
import com.fsb.pojo.entity.User;
import com.fsb.result.Result;
import com.fsb.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RolePermissionService rolePermissionService;

    @PostMapping("/login")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO userLoginDTO) throws Exception {
        User user = userService.login(userLoginDTO);

        String role = rolePermissionService.normalizeRole(user.getRole());
        List<String> permissions = rolePermissionService.resolvePermissions(role);

        AuthUser authUser = AuthUser.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(role)
                .permissions(permissions)
                .build();

        String token = jwtUtil.generateToken(authUser);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .role(role)
                .token(token)
                .permissions(permissions)
                .build();
        return Result.success(userLoginVO);
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        AuthUser authUser = AuthContext.getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("username", authUser.getUsername());
        data.put("roles", List.of(authUser.getRole()));
        data.put("permissions", authUser.getPermissions());
        data.put("menus", new ArrayList<>());
        return Result.success(data);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return Result.success();
    }
}
