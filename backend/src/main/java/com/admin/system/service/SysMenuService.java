package com.admin.system.service;

import com.admin.system.dto.MenuDTO;
import com.admin.system.vo.MenuVO;
import java.util.List;
import java.util.Map;

public interface SysMenuService {
    List<MenuVO> getTree();
    List<?> getAll();
    Object getById(Long id);
    List<Map<String, Object>> getRouters();
    void save(MenuDTO dto);
    void update(Long id, MenuDTO dto);
    void delete(Long id);
}
