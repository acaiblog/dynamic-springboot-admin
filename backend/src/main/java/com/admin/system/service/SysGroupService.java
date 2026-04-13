package com.admin.system.service;

import com.admin.system.dto.GroupDTO;
import com.admin.system.common.result.PageResult;
import java.util.List;
import java.util.Set;

public interface SysGroupService {
    PageResult<?> getPage(String name, Integer status, int page, int size);
    List<?> getAll();
    Object getById(Long id);
    void save(GroupDTO dto);
    void update(Long id, GroupDTO dto);
    void delete(Long id);
    Set<Long> getRoleIdsByGroupId(Long groupId);
    void assignRoles(Long groupId, Set<Long> roleIds);
    Set<Long> getUserIdsByGroupId(Long groupId);
    void assignUsers(Long groupId, Set<Long> userIds);
}
