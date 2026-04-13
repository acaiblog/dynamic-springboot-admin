package com.admin.system.service;

import com.admin.system.dto.RoleDTO;
import com.admin.system.common.result.PageResult;
import java.util.List;
import java.util.Set;

public interface SysRoleService {
    PageResult<?> getPage(String name, Integer status, int page, int size);
    List<?> getAll();
    Object getById(Long id);
    void save(RoleDTO dto);
    void update(Long id, RoleDTO dto);
    void delete(Long id);
    Set<Long> getMenuIdsByRoleId(Long roleId);
    void assignMenus(Long roleId, List<Long> menuIds);
}
