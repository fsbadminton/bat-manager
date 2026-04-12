package com.fsb.Controller.admin;

import com.fsb.Service.AdminService;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.auth.RolePermissionService;
import com.fsb.pojo.DTO.AdminLoginDTO;
import com.fsb.pojo.VO.AdminLoginVO;
import com.fsb.pojo.entity.Admin;
import com.fsb.result.Result;
import com.fsb.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RolePermissionService rolePermissionService;

    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO adminLoginDTO) throws Exception {
        log.info("管理员登录: {}", adminLoginDTO.getUsername());
        Admin admin = adminService.login(adminLoginDTO);

        String role = rolePermissionService.normalizeRole(admin.getRole());
        List<String> permissions = rolePermissionService.resolvePermissions(role);

        AuthUser authUser = AuthUser.builder()
                .userId(admin.getId())
                .username(admin.getUsername())
                .role(role)
                .permissions(permissions)
                .build();

        String token = jwtUtil.generateToken(authUser);

        AdminLoginVO adminLoginVO = AdminLoginVO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .name(admin.getName())
                .role(role)
                .token(token)
                .permissions(permissions)
                .build();
        return Result.success(adminLoginVO);
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
}
