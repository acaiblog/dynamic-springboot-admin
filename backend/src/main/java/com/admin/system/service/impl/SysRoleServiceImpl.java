package com.admin.system.service.impl;

import com.admin.system.common.exception.BusinessException;
import com.admin.system.common.result.PageResult;
import com.admin.system.dto.RoleDTO;
import com.admin.system.entity.SysMenu;
import com.admin.system.entity.SysRole;
import com.admin.system.repository.SysMenuRepository;
import com.admin.system.repository.SysRoleRepository;
import com.admin.system.service.SysRoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleRepository roleRepository;
    private final SysMenuRepository menuRepository;

    public SysRoleServiceImpl(SysRoleRepository roleRepository, SysMenuRepository menuRepository) {
        this.roleRepository = roleRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    public PageResult<?> getPage(String name, Integer status, int page, int size) {
        Page<SysRole> p = roleRepository.findByConditions(name, status, PageRequest.of(page - 1, size));
        List<Map<String, Object>> records = p.getContent().stream().map(this::toMap).collect(Collectors.toList());
        return PageResult.of(p.getTotalElements(), records);
    }

    @Override
    public List<?> getAll() {
        return roleRepository.findAll().stream().map(this::toMap).collect(Collectors.toList());
    }

    @Override
    public Object getById(Long id) {
        return toMap(findById(id));
    }

    private SysRole findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));
    }

    @Override
    @Transactional
    public void save(RoleDTO dto) {
        SysRole role = new SysRole();
        // 自动生成唯一编码
        String code;
        do {
            code = "ROLE_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
        } while (roleRepository.existsByCode(code));
        role.setCode(code);
        role.setName(dto.getName());
        role.setRemark(dto.getRemark());
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        if (dto.getMenuIds() != null) {
            role.setMenus(new HashSet<>(menuRepository.findAllById(dto.getMenuIds())));
        }
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void update(Long id, RoleDTO dto) {
        SysRole role = findById(id);
        // 保留原有编码，不允许修改
        role.setName(dto.getName());
        role.setRemark(dto.getRemark());
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        if (dto.getMenuIds() != null) {
            role.setMenus(new HashSet<>(menuRepository.findAllById(dto.getMenuIds())));
        }
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getMenuIdsByRoleId(Long roleId) {
        SysRole role = findById(roleId);
        return role.getMenus().stream()
                .map(SysMenu::getId)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        SysRole role = findById(roleId);
        role.setMenus(new HashSet<>(menuRepository.findAllById(menuIds)));
        roleRepository.save(role);
    }

    private void copyToEntity(RoleDTO dto, SysRole role) {
        // 保留原有编码
        role.setName(dto.getName());
        role.setRemark(dto.getRemark());
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
    }

    private Map<String, Object> toMap(SysRole role) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", role.getId());
        m.put("code", role.getCode());
        m.put("name", role.getName());
        m.put("roleName", role.getName());
        m.put("remark", role.getRemark());
        m.put("status", role.getStatus());
        m.put("createTime", role.getCreateTime());
        m.put("menuIds", new HashSet<>());
        return m;
    }
}
