package com.admin.system.service;

import com.admin.system.dto.LoginDTO;
import com.admin.system.vo.LoginVO;

public interface AuthService {
    LoginVO login(LoginDTO dto);
    LoginVO getUserInfo();
}
