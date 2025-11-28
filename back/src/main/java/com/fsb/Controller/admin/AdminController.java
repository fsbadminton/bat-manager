package com.fsb.Controller.admin;


import com.fsb.Service.AdminService;
import com.fsb.pojo.DTO.AdminLoginDTO;
import com.fsb.pojo.VO.AdminLoginVO;
import com.fsb.pojo.entity.Admin;
import com.fsb.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;


    /**
     * 管理员登录
     * @param adminLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO adminLoginDTO) throws Exception {
        log.info("管理员登录:{}", adminLoginDTO);
        Admin admin = adminService.login(adminLoginDTO);
        AdminLoginVO adminLoginVO = AdminLoginVO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .name(admin.getName())
                .role(admin.getRole())
                .token("admin-token")
                .build();
        return Result.success(adminLoginVO);
    }


    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> data = new HashMap<>();
        data.put("username", "admin");
        data.put("roles", Arrays.asList("admin"));
        data.put("menus", new ArrayList<>());

        res.put("code", 1);
        res.put("msg", null);
        res.put("data", data);

        return res;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        Map<String, Object> res = new HashMap<>();

        res.put("code", 1);
        res.put("msg", null);
        res.put("data", null);

        return res;
    }


}
