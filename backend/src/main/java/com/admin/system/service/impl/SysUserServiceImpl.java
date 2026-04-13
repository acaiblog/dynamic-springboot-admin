package com.admin.system.service.impl;

import com.admin.system.common.exception.BusinessException;
import com.admin.system.common.result.PageResult;
import com.admin.system.dto.UserDTO;
import com.admin.system.entity.SysRole;
import com.admin.system.entity.SysUser;
import com.admin.system.repository.SysRoleRepository;
import com.admin.system.repository.SysUserRepository;
import com.admin.system.service.SysUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl implements SysUserService {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public SysUserServiceImpl(SysUserRepository userRepository,
                              SysRoleRepository roleRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PageResult<?> getPage(String username, Integer status, int page, int size) {
        Page<SysUser> p = userRepository.findByConditions(username, status, PageRequest.of(page - 1, size));
        List<Map<String, Object>> records = p.getContent().stream().map(this::toMap).collect(Collectors.toList());
        return PageResult.of(p.getTotalElements(), records);
    }

    @Override
    public List<?> getAll() {
        return userRepository.findAll().stream().map(this::toMap).collect(Collectors.toList());
    }

    @Override
    public Object getById(Long id) {
        return toMap(findById(id));
    }

    private SysUser findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    @Override
    @Transactional
    public void save(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        SysUser user = new SysUser();
        copyToEntity(dto, user);
        user.setPassword(passwordEncoder.encode("123456"));
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            user.setRoles(new HashSet<>(roleRepository.findAllById(dto.getRoleIds())));
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void update(Long id, UserDTO dto) {
        SysUser user = findById(id);
        copyToEntity(dto, user);
        if (dto.getRoleIds() != null) {
            user.setRoles(new HashSet<>(roleRepository.findAllById(dto.getRoleIds())));
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("用户不存在");
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void resetPassword(Long id) {
        SysUser user = findById(id);
        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        SysUser user = findById(id);
        user.setStatus(status);
        userRepository.save(user);
    }

    private void copyToEntity(UserDTO dto, SysUser user) {
        user.setUsername(dto.getUsername());
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        user.setRemark(dto.getRemark());
    }

    public Map<String, Object> toMap(SysUser user) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", user.getId());
        m.put("username", user.getUsername());
        m.put("nickname", user.getNickname());
        m.put("email", user.getEmail());
        m.put("phone", user.getPhone());
        m.put("avatar", user.getAvatar());
        m.put("status", user.getStatus());
        m.put("remark", user.getRemark());
        m.put("createTime", user.getCreateTime());
        m.put("updateTime", user.getUpdateTime());
        m.put("roleIds", user.getRoles().stream().map(r -> r.getId()).collect(Collectors.toSet()));
        m.put("roleNames", user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()));
        return m;
    }
}
