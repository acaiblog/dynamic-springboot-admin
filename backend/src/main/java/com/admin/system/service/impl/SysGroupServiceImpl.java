package com.admin.system.service.impl;

import com.admin.system.common.exception.BusinessException;
import com.admin.system.common.result.PageResult;
import com.admin.system.dto.GroupDTO;
import com.admin.system.entity.SysGroup;
import com.admin.system.entity.SysRole;
import com.admin.system.entity.SysUser;
import com.admin.system.repository.SysGroupRepository;
import com.admin.system.repository.SysRoleRepository;
import com.admin.system.repository.SysUserRepository;
import com.admin.system.service.SysGroupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysGroupServiceImpl implements SysGroupService {

    private final SysGroupRepository groupRepository;
    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;

    public SysGroupServiceImpl(SysGroupRepository groupRepository,
                               SysUserRepository userRepository,
                               SysRoleRepository roleRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<?> getPage(String name, Integer status, int page, int size) {
        Page<SysGroup> p = groupRepository.findByConditions(name, status, PageRequest.of(page - 1, size));
        List<Map<String, Object>> records = p.getContent().stream().map(this::toMap).collect(Collectors.toList());
        return PageResult.of(p.getTotalElements(), records);
    }

    @Override
    @Transactional(readOnly = true)
    public List<?> getAll() {
        return groupRepository.findAll().stream().map(this::toMap).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Object getById(Long id) {
        return toMap(findById(id));
    }

    private SysGroup findById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户组不存在"));
    }

    @Override
    @Transactional
    public void save(GroupDTO dto) {
        SysGroup group = new SysGroup();
        // 自动生成唯一编码
        String code;
        do {
            code = "GROUP_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
        } while (groupRepository.existsByCode(code));
        group.setCode(code);
        group.setName(dto.getName());
        group.setRemark(dto.getRemark());
        group.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        if (dto.getUserIds() != null) {
            group.setUsers(new HashSet<>(userRepository.findAllById(dto.getUserIds())));
        }
        if (dto.getRoleIds() != null) {
            group.setRoles(new HashSet<>(roleRepository.findAllById(dto.getRoleIds())));
        }
        groupRepository.save(group);
    }

    @Override
    @Transactional
    public void update(Long id, GroupDTO dto) {
        SysGroup group = findById(id);
        // 保留原有编码
        group.setName(dto.getName());
        group.setRemark(dto.getRemark());
        group.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        if (dto.getUserIds() != null) {
            group.setUsers(new HashSet<>(userRepository.findAllById(dto.getUserIds())));
        }
        if (dto.getRoleIds() != null) {
            group.setRoles(new HashSet<>(roleRepository.findAllById(dto.getRoleIds())));
        }
        groupRepository.save(group);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        groupRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getRoleIdsByGroupId(Long groupId) {
        SysGroup group = findById(groupId);
        return group.getRoles().stream()
                .map(SysRole::getId)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void assignRoles(Long groupId, Set<Long> roleIds) {
        SysGroup group = findById(groupId);
        group.setRoles(new HashSet<>(roleRepository.findAllById(roleIds)));
        groupRepository.save(group);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getUserIdsByGroupId(Long groupId) {
        SysGroup group = findById(groupId);
        return group.getUsers().stream()
                .map(SysUser::getId)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void assignUsers(Long groupId, Set<Long> userIds) {
        SysGroup group = findById(groupId);
        group.setUsers(new HashSet<>(userRepository.findAllById(userIds)));
        groupRepository.save(group);
    }

    private Map<String, Object> toMap(SysGroup group) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", group.getId());
        m.put("code", group.getCode());
        m.put("name", group.getName());
        m.put("remark", group.getRemark());
        m.put("status", group.getStatus());
        m.put("createTime", group.getCreateTime());
        m.put("userIds", new HashSet<Long>());
        m.put("userNames", new HashSet<String>());
        m.put("roleIds", new HashSet<Long>());
        m.put("roleNames", new HashSet<String>());
        return m;
    }
}
