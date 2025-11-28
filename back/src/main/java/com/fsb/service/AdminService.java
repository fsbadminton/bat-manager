package com.fsb.Service;

import com.fsb.pojo.DTO.AdminLoginDTO;
import com.fsb.pojo.entity.Admin;

public interface AdminService {

    Admin login(AdminLoginDTO adminLoginDTO) throws Exception;
}
