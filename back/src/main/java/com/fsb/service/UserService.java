package com.fsb.Service;

import com.fsb.pojo.DTO.UserLoginDTO;
import com.fsb.pojo.VO.UserVO;
import com.fsb.pojo.entity.User;

public interface UserService {
    User login(UserLoginDTO userLoginDTO) throws Exception;
}
