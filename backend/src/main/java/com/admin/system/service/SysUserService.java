package com.admin.system.service;

import com.admin.system.dto.UserDTO;
import com.admin.system.common.result.PageResult;
import java.util.List;

public interface SysUserService {
    PageResult<?> getPage(String username, Integer status, int page, int size);
    List<?> getAll();
    Object getById(Long id);
    void save(UserDTO dto);
    void update(Long id, UserDTO dto);
    void delete(Long id);
    void resetPassword(Long id);
    void updateStatus(Long id, Integer status);
}
